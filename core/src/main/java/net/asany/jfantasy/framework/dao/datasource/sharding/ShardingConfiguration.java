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
package net.asany.jfantasy.framework.dao.datasource.sharding;

import com.google.common.collect.Lists;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.datasource.MultiDataSourceManager;
import net.asany.jfantasy.framework.dao.datasource.MultiDataSourceOperations;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;

/**
 * 分片数据源配置
 *
 * @author limaofeng
 */
@Slf4j
public class ShardingConfiguration {

  @Getter private final DataSource primaryDataSource;
  private final List<ShardingStrategyCustomizer> customizers;

  private final MultiDataSourceManager dataSourceManager;
  private DataSource shardingDataSource;

  public ShardingConfiguration(
      DataSource dataSource,
      MultiDataSourceManager dataSourceManager,
      List<ShardingStrategyCustomizer> customizers) {
    this.primaryDataSource = dataSource;
    this.dataSourceManager = dataSourceManager;
    this.customizers = customizers;
  }

  public void init() {
    this.shardingDataSource = buildDataSource();
    this.dataSourceManager.on(
        MultiDataSourceOperations.ADD_DATA_SOURCE,
        event -> this.shardingDataSource = buildDataSource());
    this.dataSourceManager.on(
        MultiDataSourceOperations.REMOVE_DATA_SOURCE,
        event -> this.shardingDataSource = buildDataSource());
  }

  @SneakyThrows(SQLException.class)
  private DataSource buildDataSource() {
    ShardingRuleConfiguration conf = new ShardingRuleConfiguration();
    conf.setTableRuleConfigs(Lists.newArrayList());

    for (ShardingStrategyCustomizer customizer : customizers) {
      customizer.customize(conf);
    }

    Properties props = new Properties();
    props.put("sql.show", true);

    Map<String, DataSource> dataSourceMap = dataSourceManager.getAllDataSources();
    dataSourceMap.put("primary", primaryDataSource);
    conf.setDefaultDataSourceName("primary");

    return ShardingDataSourceFactory.createDataSource(dataSourceMap, conf, props);
  }

  public void destroy() {
    if (shardingDataSource instanceof ShardingDataSource) {
      try {
        ((ShardingDataSource) shardingDataSource).close();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  public DataSource getDataSource() {
    return shardingDataSource;
  }
}
