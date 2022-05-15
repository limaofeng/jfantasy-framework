package org.jfantasy.framework.search;

import lombok.Setter;
import lombok.SneakyThrows;
import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.cache.DaoCache;
import org.jfantasy.framework.search.cache.IndexCache;
import org.jfantasy.framework.search.config.IndexedScanner;
import org.jfantasy.framework.search.dao.DataFetcher;
import org.jfantasy.framework.search.dao.JpaDefaultDataFetcher;
import org.jfantasy.framework.search.elastic.ElasticCuckooIndex;
import org.jfantasy.framework.search.elastic.ElasticsearchConnection;
import org.jfantasy.framework.search.exception.ElasticsearchConnectionException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CuckooIndexFactory
    implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(CuckooIndexFactory.class);

  private static CuckooIndexFactory instance;
  /** RAMBufferSizeMB */
  private double bufferSizeMB = 16.0D;
  /** Lucene 版本 */
  // private Version version = Version.LUCENE_36;
  /** 分词器 */
  // private Analyzer analyzer = new StandardAnalyzer(this.version);
  /** 索引文件的存放目录 */
  /** 集群配置 */
  //  private ClusterConfig clusterConfig;
  /** 线程池 */
  private SchedulingTaskExecutor executor;

  private boolean rebuild = false;

  private final Map<Class<?>, IndexRebuilder> indexRebuilds = new HashMap<>();

  @Setter private String hostname;
  @Setter private int port;
  @Setter private String sslCertificatePath;
  @Setter private String apiKey;
  @Setter private String username;
  @Setter private String password;

  private ApplicationContext applicationContext;
  private ElasticsearchConnection connection;

  @Async
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    this.initialize();
  }

  @SneakyThrows
  public void initialize() {
    LOG.debug("Starting Lucene");
    StopWatch watch = new StopWatch();
    watch.start();

    this.connection = this.makeConnection();

    Set<Class<?>> indexedClasses = new IndexedScanner(applicationContext).scan(Document.class);

    DaoCache daoCache = DaoCache.getInstance();
    IndexCache indexCache = IndexCache.getInstance();

    for (Class<?> clazz : indexedClasses) {
      DataFetcher dataFetcher = buildDataFetcher(applicationContext, clazz);
      CuckooIndex cuckooIndex = this.createIndex(clazz, dataFetcher, this.connection);

      daoCache.put(clazz, dataFetcher);
      indexCache.put(clazz, cuckooIndex);
      indexRebuilds.put(clazz, new IndexRebuilder(clazz, this.executor, 100));
    }

    LOG.debug("Started Lucene in {} ms", watch.getTotalTimeMillis());
  }

  private synchronized ElasticsearchConnection makeConnection() {
    ElasticsearchConnection connection = new ElasticsearchConnection(this.hostname, this.port);

    if (this.sslCertificatePath != null) {
      connection.setSslCertificatePath(sslCertificatePath);
    }

    return connection;
  }

  private CuckooIndex createIndex(
      Class<?> clazz, DataFetcher dataFetcher, ElasticsearchConnection connection) {
    return new ElasticCuckooIndex(clazz, dataFetcher, connection);
  }

  private DataFetcher buildDataFetcher(ApplicationContext applicationContext, Class<?> clazz) {
    Document document = clazz.getAnnotation(Document.class);
    Class<? extends DataFetcher> daoClass = document.fetcher();

    if (ClassUtil.isAssignable(JpaDefaultDataFetcher.class, daoClass)) {
      return ClassUtil.newInstance(
          daoClass,
          new Class[] {ApplicationContext.class, Class.class},
          new Object[] {applicationContext, clazz});
    }
    boolean existent = applicationContext.getBeanNamesForType(daoClass).length > 0;
    if (existent) {
      return applicationContext.getBean(daoClass);
    }
    return ClassUtil.newInstance(daoClass);
  }

  private void rebuild() {
    for (IndexRebuilder indexRebuilder : indexRebuilds.values()) {
      indexRebuilder.rebuild();
    }
  }

  public void rebuild(Class<?> clazz) {
    indexRebuilds.get(clazz).rebuild();
  }

  /** 初始化方法 */
  public void open() throws IOException {
    if (this.apiKey != null) {
      this.connection.connect(this.apiKey);
    } else if (this.username != null && this.password != null) {
      this.connection.connect(this.username, this.password);
    } else {
      throw new ElasticsearchConnectionException("Elasticsearch 连接失败,未配置授权");
    }
    Map<Class, CuckooIndex> indexMap = IndexCache.getInstance().getAll();
    for (Map.Entry<Class, CuckooIndex> entry : indexMap.entrySet()) {
      CuckooIndex cuckooIndex = entry.getValue();
      cuckooIndex.createIndex();
    }

    if (this.rebuild) {
      this.rebuild();
    }
  }

  /** 关闭方法 */
  public void close() throws IOException {
    this.connection.close();
    // Map<String, IndexWriter> map = IndexWriterCache.getInstance().getAll();
    // for (IndexWriter writer : map.values()) {
    // if (writer != null) {
    // this.closeIndexWriter(writer);
    // }
    // }
  }

  public double getBufferSizeMB() {
    return this.bufferSizeMB;
  }

  public void setBufferSizeMB(double bufferSizeMB) {
    this.bufferSizeMB = bufferSizeMB;
  }

  //  public ClusterConfig getClusterConfig() {
  //    return this.clusterConfig;
  //  }
  //
  //  public void setClusterConfig(ClusterConfig clusterConfig) {
  //    this.clusterConfig = clusterConfig;
  //  }

  public void setRebuild(boolean rebuild) {
    this.rebuild = rebuild;
  }

  //  public File getOpenFolder(String remotePath) {
  //    return FileUtil.createFolder(this.directoryPath + remotePath);
  //  }

  public void setExecutor(SchedulingTaskExecutor executor) {
    this.executor = executor;
  }

  public static boolean isRunning() {
    return CuckooIndexFactory.instance != null;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
