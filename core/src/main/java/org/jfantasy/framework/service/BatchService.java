package org.jfantasy.framework.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.jfantasy.framework.service.batch.DefaultBatchService;

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
   * 提交
   *
   * @param entity 实体
   * @return CompletableFuture<R>
   */
  CompletableFuture<R> submit(T entity);
}
