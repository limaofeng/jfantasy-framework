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
   * @param saver 保存方法
   * @param batchSize 批处理大小
   * @param works 工人数量
   * @param <T> 类型
   * @return BatchService<T>
   */
  static <T, R> BatchService<T, R> create(
      Function<List<T>, List<R>> saver, int batchSize, int works) {
    return new DefaultBatchService<>(saver, batchSize, works);
  }

  /**
   * 提交
   *
   * @param entity 实体
   * @return CompletableFuture<R>
   */
  CompletableFuture<R> submit(T entity);
}
