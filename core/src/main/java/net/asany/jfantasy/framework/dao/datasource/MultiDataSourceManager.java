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
