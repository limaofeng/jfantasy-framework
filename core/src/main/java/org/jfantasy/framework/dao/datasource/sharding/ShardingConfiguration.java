package org.jfantasy.framework.dao.datasource.sharding;

import com.google.common.collect.Lists;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.jfantasy.framework.dao.datasource.MultiDataSourceManager;
import org.jfantasy.framework.dao.datasource.MultiDataSourceOperations;

/**
 * 分片数据源配置
 *
 * @author limaofeng
 */
@Slf4j
public class ShardingConfiguration {

  private final DataSource primaryDataSource;
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

  public DataSource getPrimaryDataSource() {
    return primaryDataSource;
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
