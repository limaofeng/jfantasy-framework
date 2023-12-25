package net.asany.jfantasy.framework.service.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.service.BatchService;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalance;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalanceMetrics;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 批量提交服务
 *
 * @param <T>
 * @param <R>
 * @author limaofeng
 */
@Slf4j
public abstract class DefaultBatchService<T, R> implements BatchService<T, R> {

  private final String taskName;

  public ConcurrentHashMap<String, Worker<T, R>> cache = new ConcurrentHashMap<>();

  /** 工人数量 */
  private int workerNumber;

  /** 批处理大小 */
  private int batchSize;

  /** 线程池 */
  private final Executor executor;

  private final LoadBalance loadBalance;

  private final LoadBalanceMetrics metrics;

  public static Executor asyncServiceExecutor(int poolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(poolSize);
    executor.setMaxPoolSize(poolSize);
    executor.setQueueCapacity(poolSize);
    executor.setKeepAliveSeconds(180);
    executor.setThreadNamePrefix("bath_service");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  public DefaultBatchService(String taskName, int batchSize, int works) {
    this(taskName, batchSize, works, asyncServiceExecutor(works), null, null);
  }

  public DefaultBatchService(
      String taskName,
      int batchSize,
      int works,
      Executor executor,
      LoadBalance loadBalance,
      LoadBalanceMetrics metrics) {
    this.taskName = taskName;
    this.workerNumber = works;
    this.batchSize = batchSize;
    this.executor = executor;
    this.metrics = metrics == null ? new LoadBalanceMetrics() : metrics;
    this.loadBalance = loadBalance == null ? LoadBalance.roundRobin(this.metrics) : loadBalance;
    this.init();
  }

  public DefaultBatchService(String taskName, int batchSize, int works, Executor executor) {
    this(taskName, batchSize, works, executor, null, null);
  }

  /**
   * 执行任务
   *
   * @param entities 数据
   * @return 返回结果
   */
  protected abstract List<R> run(List<T> entities);

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
    this.cache.forEach((k, v) -> v.setBatchSize(batchSize));
  }

  public void setWorkerNumber(int workerNumber) {
    if (this.workerNumber == workerNumber) {
      return;
    }
    if (this.workerNumber < workerNumber) {
      for (int i = this.workerNumber; i < workerNumber; i++) {
        Worker<T, R> task = new Worker<>(this.taskName + "(" + i + ")", this::run, batchSize);
        cache.put(String.valueOf(i), task);
        log.debug("start worker " + i);
        executor.execute(task);
      }
    }
    if (this.workerNumber > workerNumber) {
      for (int i = this.workerNumber - 1; i >= workerNumber; i--) {
        Worker<T, R> task = cache.remove(String.valueOf(i));
        log.debug("shutdown worker " + i);
        metrics.removeServer(String.valueOf(i));
        task.shutdown();
      }
    }
    this.workerNumber = workerNumber;
  }

  public int getWorkerNumber() {
    return workerNumber;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void init() {
    for (int i = 0; i < workerNumber; i++) {
      Worker<T, R> task = new Worker<>(taskName + "(" + i + ")", this::run, batchSize);
      cache.put(String.valueOf(i), task);
      executor.execute(task);
    }
  }

  @Override
  public CompletableFuture<R> submit(T entity) {
    List<String> keys = new ArrayList<>(cache.keySet());
    String key = loadBalance.select(keys);
    Worker<T, R> queue = cache.get(key);
    metrics.incrementRequest(key);
    CompletableFuture<R> value = new CompletableFuture<>();
    queue
        .add(entity)
        .exceptionally(
            e -> {
              metrics.incrementError(key, e);
              value.obtrudeException(e);
              return null;
            })
        .thenAccept(
            (r) -> {
              metrics.incrementSuccess(key);
              value.complete(r);
            });
    return value;
  }
}
