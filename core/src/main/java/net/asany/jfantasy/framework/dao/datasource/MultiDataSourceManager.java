package net.asany.jfantasy.framework.dao.datasource;

import java.util.Map;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

/**
 * 多数据源工厂
 *
 * @author limaofeng
 */
public interface MultiDataSourceManager {

  /**
   * 获取数据源
   *
   * @param dataSourceKey 数据源key
   * @return DataSource
   */
  DataSource getDataSource(String dataSourceKey);

  /**
   * 获取所有数据源
   *
   * @return Map
   */
  Map<String, DataSource> getAllDataSources();

  /**
   * 添加数据源
   *
   * @param name 数据源名称
   * @param dataSource 数据源
   */
  void addDataSource(String name, DataSource dataSource);

  /**
   * 添加数据源
   *
   * @param properties 数据源配置
   */
  void addDataSource(DataSourceProperties properties);

  /**
   * 移除数据源
   *
   * @param name 数据源名称
   */
  void removeDataSource(String name);

  /**
   * 监听数据源事件
   *
   * @param event 事件
   * @param callback 回调函数
   */
  void on(MultiDataSourceOperations event, Consumer<MultiDataSourceEvent> callback);
}
