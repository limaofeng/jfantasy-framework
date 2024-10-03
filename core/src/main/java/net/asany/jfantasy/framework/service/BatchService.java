/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.asany.jfantasy.framework.service.batch.DefaultBatchService;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalance;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalanceMetrics;

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
      case "random" -> loadBalancer = LoadBalance.random();
      case "least" -> loadBalancer = LoadBalance.leastConnection(metrics);
      default -> loadBalancer = LoadBalance.roundRobin(metrics);
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
