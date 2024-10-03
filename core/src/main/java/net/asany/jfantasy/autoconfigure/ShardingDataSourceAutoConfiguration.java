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
package net.asany.jfantasy.autoconfigure;

import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import net.asany.jfantasy.autoconfigure.properties.DataSourceExtendedProperties;
import net.asany.jfantasy.framework.dao.datasource.MultiDataSource;
import net.asany.jfantasy.framework.dao.datasource.MultiDataSourceManager;
import net.asany.jfantasy.framework.dao.datasource.sharding.ShardingConfiguration;
import net.asany.jfantasy.framework.dao.datasource.sharding.ShardingDataSourceProxy;
import net.asany.jfantasy.framework.dao.datasource.sharding.ShardingStrategyCustomizer;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
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
