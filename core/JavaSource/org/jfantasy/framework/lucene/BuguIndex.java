package org.jfantasy.framework.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.lucene.annotations.Indexed;
import org.jfantasy.framework.lucene.backend.IndexChecker;
import org.jfantasy.framework.lucene.backend.IndexReopenTask;
import org.jfantasy.framework.lucene.cache.DaoCache;
import org.jfantasy.framework.lucene.cache.IndexWriterCache;
import org.jfantasy.framework.lucene.cluster.ClusterConfig;
import org.jfantasy.framework.lucene.dao.LuceneDao;
import org.jfantasy.framework.lucene.dao.hibernate.HibernateLuceneDao;
import org.jfantasy.framework.spring.ClassPathScanner;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.PathUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BuguIndex implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(BuguIndex.class);

    private static BuguIndex instance;
    /**
     * RAMBufferSizeMB
     */
    private double bufferSizeMB = 16.0D;
    /**
     * Lucene 版本
     */
    private Version version = Version.LUCENE_36;
    /**
     * 分词器
     */
    private Analyzer analyzer = new StandardAnalyzer(this.version);
    /**
     * 索引文件的存放目录
     */
    private String directoryPath;
    /**
     * 集群配置
     */
    private ClusterConfig clusterConfig;
    /**
     * 线程池
     */
    private SchedulingTaskExecutor executor;
    /**
     * reopen 执行周期
     */
    private long period = 30000L;

    private boolean rebuild = false;

    private Map<Class<?>, IndexRebuilder> indexRebuilders = new HashMap<>();

    private String[] packagesToScan = new String[]{"org.jfantasy"};

    public static synchronized BuguIndex getInstance() {
        if (instance == null) {
            throw new NotFoundException(" BuguIndex 未初始化 .");
        }
        return instance;
    }

    @Async
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (BuguIndex.instance == null) {
            this.afterPropertiesSet();
        }
    }

    private void scanDao(Set<Class<?>> indexedClasses, String basePackage) {
        if (!SpringContextUtil.startup()) {
            return;
        }
        for (Class<?> clazz : ClassPathScanner.getInstance().findInterfaceClasses(basePackage, HibernateDao.class)) {
            Class entityClass = ClassUtil.getSuperClassGenricType(clazz);
            if (entityClass.getAnnotation(Indexed.class) == null) {
                continue;
            }
            LuceneDao dao = createHibernateLuceneDao(entityClass.getSimpleName(), (HibernateDao) SpringContextUtil.getBeanByType(clazz));
            indexedClasses.add(entityClass);
            DaoCache.getInstance().put(entityClass, dao);
        }
    }

    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        LOG.debug("Starting Lucene");
        StopWatch watch = new StopWatch();
        watch.start();
        Set<Class<?>> indexedClasses = new LinkedHashSet<>();
        for (String basePackage : packagesToScan) {
            scanDao(indexedClasses, basePackage);
        }
        for (Class<?> clazz : indexedClasses) {
            indexRebuilders.put(clazz, new IndexRebuilder(clazz));
        }
        if (BuguIndex.instance == null) {
            BuguIndex.instance = this;//NOSONAR
        }
        if (this.rebuild) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    BuguIndex.this.rebuild();
                }
            }, period);
        }
        LOG.debug("Started Lucene in {} ms", watch.getTotalTimeMillis());
    }

    private static LuceneDao createHibernateLuceneDao(String name, HibernateDao hibernateDao) {
        return SpringContextUtil.registerBeanDefinition(name + "HibernateLuceneDao", HibernateLuceneDao.class, new Object[]{hibernateDao});
    }

    public void rebuild() {
        for (IndexRebuilder indexRebuilder : indexRebuilders.values()) {
            indexRebuilder.rebuild();
        }
    }

    public void rebuild(Class<?> clazz) {
        if (!IndexChecker.hasIndexed(clazz)) {
            throw new IgnoreException(clazz + " @Indexed ");
        }
        if (!this.indexRebuilders.containsKey(clazz)) {
            throw new IgnoreException(clazz + " not found indexRebuilder");
        }
        indexRebuilders.get(clazz).rebuild();
    }

    /**
     * 初始化方法
     */
    public void open() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new IndexReopenTask(), this.period, this.period, TimeUnit.MILLISECONDS);
        if (this.clusterConfig != null) {
            this.clusterConfig.validate();
        }
    }

    private void closeIndexWriter(IndexWriter writer) {
        Directory dir = writer.getDirectory();
        try {
            writer.commit();
            writer.close();
        } catch (IOException ex) {
            LOG.error("Can not commit and close the lucene index", ex);
        } finally {
            try {
                if ((dir != null) && (IndexWriter.isLocked(dir))) {
                    IndexWriter.unlock(dir);
                }
            } catch (IOException ex) {
                LOG.error("Can not unlock the lucene index", ex);
            }
        }
    }

    /**
     * 关闭方法
     */
    public void close() {
        if (this.clusterConfig != null) {
            this.clusterConfig.invalidate();
        }
        Map<String, IndexWriter> map = IndexWriterCache.getInstance().getAll();
        for (IndexWriter writer : map.values()) {
            if (writer != null) {
                this.closeIndexWriter(writer);
            }
        }
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public double getBufferSizeMB() {
        return this.bufferSizeMB;
    }

    public void setBufferSizeMB(double bufferSizeMB) {
        this.bufferSizeMB = bufferSizeMB;
    }

    public void setIndexReopenPeriod(long period) {
        this.period = period;
    }

    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = ObjectUtil.defaultValue(analyzer,this.analyzer);
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public ClusterConfig getClusterConfig() {
        return this.clusterConfig;
    }

    public void setClusterConfig(ClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig;
    }

    public void setBasePackage(String basePackage) {
        this.packagesToScan = StringUtils.tokenizeToStringArray(basePackage, ",; \t\n");
    }

    public void setRebuild(boolean rebuild) {
        this.rebuild = rebuild;
    }

    public File getOpenFolder(String remotePath) {
        return FileUtil.createFolder(StringUtil.defaultValue(PathUtil.webinf(), PathUtil.classes()) + this.directoryPath + remotePath);
    }

    public void setExecutor(SchedulingTaskExecutor executor) {
        this.executor = executor;
    }

    public static boolean isRunning() {
        return BuguIndex.instance != null;
    }
}