package org.jfantasy.framework.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.cache.DaoCache;
import org.jfantasy.framework.search.cache.IndexCache;
import org.jfantasy.framework.search.config.IndexedScanner;
import org.jfantasy.framework.search.dao.CuckooDao;
import org.jfantasy.framework.search.elastic.ElasticCuckooIndex;
import org.jfantasy.framework.search.elastic.ElasticsearchConnection;
import org.jfantasy.framework.search.exception.ElasticsearchConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.StopWatch;

public class CuckooIndexFactory implements ApplicationContextAware {

  private static final Logger LOG = LoggerFactory.getLogger(CuckooIndexFactory.class);
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

  private int batchSize;
  private ApplicationContext applicationContext;
  private ElasticsearchConnection connection;

  @SneakyThrows
  public void initialize() {
    LOG.info("Starting CuckooIndex");
    StopWatch watch = new StopWatch();
    watch.start();

    this.connection = this.makeConnection();

    Set<Class<?>> indexedClasses = new IndexedScanner(applicationContext).scan(Document.class);
    DaoCache daoCache = DaoCache.getInstance(this.applicationContext, this.executor);
    IndexCache indexCache = IndexCache.getInstance();

    if (this.apiKey != null) {
      this.connection.connect(this.apiKey);
    } else if (this.username != null && this.password != null) {
      this.connection.connect(this.username, this.password);
    } else {
      throw new ElasticsearchConnectionException("Elasticsearch 连接失败,未配置授权");
    }

    for (Class<?> clazz : indexedClasses) {
      CuckooDao cuckooDao = daoCache.buildDao(clazz);
      CuckooIndex cuckooIndex = this.createIndex(clazz, cuckooDao, this.connection);

      daoCache.put(clazz, cuckooDao);
      indexCache.put(clazz, cuckooIndex);
      indexRebuilds.put(clazz, new IndexRebuilder(clazz, this.executor, this.batchSize));
    }

    if (this.rebuild) {
      executor.execute(CuckooIndexFactory.this::rebuild, 1000 * 30);
    }

    LOG.info("Started CuckooIndex in {} ms", watch.getTotalTimeMillis());
  }

  private synchronized ElasticsearchConnection makeConnection() {
    ElasticsearchConnection connection = new ElasticsearchConnection(this.hostname, this.port);

    if (this.sslCertificatePath != null) {
      connection.setSslCertificatePath(sslCertificatePath);
    }

    return connection;
  }

  private CuckooIndex createIndex(
      Class<?> clazz, CuckooDao cuckooDao, ElasticsearchConnection connection) throws IOException {
    return new ElasticCuckooIndex(clazz, cuckooDao, connection, this.batchSize);
  }

  private void rebuild() {
    for (IndexRebuilder indexRebuilder : indexRebuilds.values()) {
      indexRebuilder.rebuild();
    }
  }

  public void rebuild(Class<?> clazz) {
    indexRebuilds.get(clazz).rebuild();
  }

  /** 关闭方法 */
  public void destroy() throws IOException {
    this.connection.close();
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public void setRebuild(boolean rebuild) {
    this.rebuild = rebuild;
  }

  public void setExecutor(SchedulingTaskExecutor executor) {
    this.executor = executor;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
