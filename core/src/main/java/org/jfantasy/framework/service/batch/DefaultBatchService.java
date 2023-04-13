package org.jfantasy.framework.service.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.service.BatchService;
import org.jfantasy.framework.service.loadbalance.LoadBalance;
import org.jfantasy.framework.service.loadbalance.LoadBalanceMetrics;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 批量提交服务
 *
 * @author limaofeng
 * @param <T>
 * @param <R>
 */
@Slf4j
public class DefaultBatchService<T, R> implements BatchService<T, R> {

  public ConcurrentHashMap<String, Worker<T, R>> cache = new ConcurrentHashMap<>();

  /** 工人数量 */
  private int workerNumber;
  /** 批处理大小 */
  private int batchSize;

  /** 线程池 */
  private final Executor executor;

  private final LoadBalance loadBalance;

  private final LoadBalanceMetrics metrics;
  private Function<List<T>, List<R>> saver;

  public static Executor asyncServiceExecutor(int poolSize) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(poolSize);
    executor.setMaxPoolSize(poolSize);
    executor.setQueueCapacity(poolSize);
    executor.setKeepAliveSeconds(30);
    executor.setThreadNamePrefix("bath_service");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  public DefaultBatchService(Function<List<T>, List<R>> saver, int batchSize, int works) {
    this(saver, batchSize, works, asyncServiceExecutor(works), null, null);
  }

  public DefaultBatchService(
      Function<List<T>, List<R>> saver,
      int batchSize,
      int works,
      Executor executor,
      LoadBalance loadBalance,
      LoadBalanceMetrics metrics) {
    this.workerNumber = works;
    this.batchSize = batchSize;
    this.executor = executor;
    this.metrics = metrics == null ? new LoadBalanceMetrics() : metrics;
    this.loadBalance = loadBalance == null ? LoadBalance.roundRobin(this.metrics) : loadBalance;
    if (saver != null) {
      this.init(saver);
    }
  }

  public DefaultBatchService(int batchSize, int works) {
    this(batchSize, works, asyncServiceExecutor(works));
  }

  public DefaultBatchService(int batchSize, int works, Executor executor) {
    this(null, batchSize, works, executor, null, null);
  }

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
        Worker<T, R> task = new Worker<>(saver, batchSize);
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

  public void init(Function<List<T>, List<R>> saver) {
    for (int i = 0; i < workerNumber; i++) {
      Worker<T, R> task = new Worker<>(saver, batchSize);
      cache.put(String.valueOf(i), task);
      executor.execute(task);
    }
    this.saver = saver;
  }

  @Override
  public CompletableFuture<R> submit(T entity) {
    List<String> keys = new ArrayList<>(cache.keySet());
    String key = loadBalance.select(keys);
    Worker<T, R> queue = cache.get(key);
    metrics.incrementRequest(key);
    return queue
        .add(entity)
        .thenApply(
            r -> {
              metrics.incrementSuccess(key);
              return r;
            })
        .exceptionally(
            e -> {
              metrics.incrementError(key, e);
              return null;
            });
  }
}
