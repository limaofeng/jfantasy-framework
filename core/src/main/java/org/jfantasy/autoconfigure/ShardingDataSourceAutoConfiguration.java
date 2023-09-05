package org.jfantasy.autoconfigure;

import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.jfantasy.autoconfigure.properties.DataSourceExtendedProperties;
import org.jfantasy.framework.dao.datasource.MultiDataSource;
import org.jfantasy.framework.dao.datasource.MultiDataSourceManager;
import org.jfantasy.framework.dao.datasource.sharding.ShardingConfiguration;
import org.jfantasy.framework.dao.datasource.sharding.ShardingDataSourceProxy;
import org.jfantasy.framework.dao.datasource.sharding.ShardingStrategyCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceUnwrapper;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Sharding 数据源自动配置
 *
 * @author limaofeng
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnExpression("${spring.datasource.sharding:false}")
@ConditionalOnBean(MultiDataSourceManager.class)
@EnableConfigurationProperties({
  DataSourceExtendedProperties.class,
})
public class ShardingDataSourceAutoConfiguration {

  @Bean(initMethod = "init", destroyMethod = "destroy")
  public ShardingConfiguration shardingConfiguration(
      @Autowired @Qualifier("dataSource") DataSource dataSource,
      @Autowired(required = false) MultiDataSourceManager dataSourceManager,
      List<ShardingStrategyCustomizer> customizers) {
    return new ShardingConfiguration(dataSource, dataSourceManager, customizers);
  }

  @Bean
  @Primary
  @ConditionalOnExpression("${spring.datasource.sharding:false}")
  public DataSource shardingDataSource(
      DataSourceExtendedProperties extendedProperties,
      @Autowired @Qualifier("dataSource") DataSource dataSource,
      MultiDataSourceManager dataSourceManager,
      @Autowired(required = false) MultiDataSource.CatalogConverter catalogConverter,
      @Autowired(required = false) ShardingConfiguration shardingConfiguration) {
    if (extendedProperties.isSharding()) {
      return new ShardingDataSourceProxy(shardingConfiguration);
    }
    return new MultiDataSource(dataSource, dataSourceManager, catalogConverter);
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(ShardingDataSource.class)
  static class ShardingDataSourceMetadataProviderConfiguration {

    @Bean
    DataSourcePoolMetadataProvider shardingDataSourceMetadataProvider() {
      return (dataSource) -> {
        ShardingDataSourceProxy shardingDataSource =
            DataSourceUnwrapper.unwrap(dataSource, ShardingDataSourceProxy.class);
        if (shardingDataSource == null) {
          return null;
        }
        DataSource primaryDataSource = shardingDataSource.getPrimaryDataSource();
        if (primaryDataSource instanceof HikariDataSource) {
          return new HikariDataSourcePoolMetadata((HikariDataSource) primaryDataSource);
        }
        return null;
      };
    }
  }
}
