package org.jfantasy.framework.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.jfantasy.framework.service.batch.DefaultBatchService;
import org.jfantasy.framework.service.loadbalance.LoadBalance;
import org.jfantasy.framework.service.loadbalance.LoadBalanceMetrics;

/**
 * 批量提交服务
 *
 * @author limaofeng
 */
public interface BatchService<T, R> {

  /**
   * 生成批量提交服务
   *
   * @param taskName 任务名称
   * @param saver 保存方法
   * @param batchSize 批处理大小
   * @param works 工人数量
   * @param <T> 类型
   * @return BatchService<T>
   */
  static <B extends BatchService<T, R>, T, R> B create(
      String taskName, Function<List<T>, List<R>> saver, int batchSize, int works) {
    //noinspection unchecked
    return (B)
        new DefaultBatchService<T, R>(taskName, batchSize, works) {
          @Override
          protected List<R> run(List<T> entities) {
            return saver.apply(entities);
          }
        };
  }

  /**
   * 生成批量提交服务
   *
   * @param taskName 任务名称
   * @param saver 保存方法
   * @param loadBalance 负载均衡
   * @param batchSize 批处理大小
   * @param works 工人数量
   * @param <B> 类型
   * @param <T> 类型
   * @param <R> 类型
   * @return BatchService<T>
   */
  static <B extends BatchService<T, R>, T, R> B create(
      String taskName,
      Function<List<T>, List<R>> saver,
      String loadBalance,
      int batchSize,
      int works) {
    LoadBalanceMetrics metrics = new LoadBalanceMetrics();
    LoadBalance loadBalancer;
    switch (loadBalance) {
      case "random":
        loadBalancer = LoadBalance.random();
        break;
      case "least":
        loadBalancer = LoadBalance.leastConnection(metrics);
        break;
      case "round":
      default:
        loadBalancer = LoadBalance.roundRobin(metrics);
        break;
    }
    //noinspection unchecked
    return (B)
        new DefaultBatchService<T, R>(taskName, batchSize, works, null, loadBalancer, metrics) {
          @Override
          protected List<R> run(List<T> entities) {
            return saver.apply(entities);
          }
        };
  }

  /**
   * 提交
   *
   * @param entity 实体
   * @return CompletableFuture<R>
   */
  CompletableFuture<R> submit(T entity);
}
