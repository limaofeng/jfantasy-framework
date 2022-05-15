package org.jfantasy.framework.search.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jfantasy.framework.search.dao.DataFetcher;
import org.jfantasy.framework.search.dao.JpaDefaultDataFetcher;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Document {
  /**
   * elasticsearch 索引名称
   *
   * @return String
   */
  String indexName();

  /**
   * 配置是否创建索引
   *
   * @return boolean
   */
  boolean createIndex() default true;

  /**
   * 文档数据加载器
   *
   * @return Class<? extends DataFetcher>
   */
  Class<? extends DataFetcher> fetcher() default JpaDefaultDataFetcher.class;
}
