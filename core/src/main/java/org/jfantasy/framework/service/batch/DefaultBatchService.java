package org.jfantasy.framework.service.batch;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.jfantasy.framework.service.BatchService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class DefaultBatchService<T, R> implements BatchService<T, R> {

  public AtomicInteger atomicInteger = new AtomicInteger(0);
  public ConcurrentHashMap<Integer, Worker<T, R>> cache = new ConcurrentHashMap<>();

  private final int workerNumber;
  private final int batchSize;

  private final Executor executor;

  public Executor asyncServiceExecutor(int poolSize) {
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
    this.workerNumber = works;
    this.batchSize = batchSize;
    this.executor = asyncServiceExecutor(this.workerNumber);
    this.init(saver);
  }

  public DefaultBatchService(
      Function<List<T>, List<R>> saver, int batchSize, int works, Executor executor) {
    this.workerNumber = works;
    this.batchSize = batchSize;
    this.executor = executor;
    this.init(saver);
  }

  public DefaultBatchService(int batchSize, int works) {
    this.workerNumber = works;
    this.batchSize = batchSize;
    this.executor = asyncServiceExecutor(this.workerNumber);
  }

  public DefaultBatchService(int batchSize, int works, Executor executor) {
    this.workerNumber = works;
    this.batchSize = batchSize;
    this.executor = executor;
  }

  public void init(Function<List<T>, List<R>> saver) {
    for (int i = 0; i < workerNumber; i++) {
      Worker<T, R> task = new Worker<>(saver, batchSize);
      cache.put(i, task);
      executor.execute(task);
    }
  }

  public final int getAndIncrement() {
    int current;
    int next;
    do {
      current = this.atomicInteger.get();
      next = current >= 214748364 ? 0 : current + 1;
    } while (!this.atomicInteger.compareAndSet(current, next));
    return next;
  }

  @Override
  public CompletableFuture<R> submit(T entity) {
    int count = getAndIncrement();
    int position = count % workerNumber;
    Worker<T, R> queue = cache.get(position);
    return queue.add(entity);
  }
}
