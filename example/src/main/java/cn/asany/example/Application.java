package cn.asany.example;

import cn.asany.example.autoconfigure.MyDatabaseShardingAlgorithm;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.jfantasy.framework.dao.datasource.AbstractMultiDataSourceManager;
import org.jfantasy.framework.dao.datasource.MultiDataSourceManager;
import org.jfantasy.framework.dao.datasource.sharding.ShardingStrategyCustomizer;
import org.jfantasy.framework.dao.jpa.ComplexJpaRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 启动器
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019/2/13 4:04 PM
 */
@Slf4j
@EnableCaching
@Configuration
@ComponentScan("cn.asany.example.demo")
@EntityScan({"cn.asany.example.*.domain"})
@EnableJpaRepositories(
    includeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          value = {JpaRepository.class})
    },
    basePackages = {
      "cn.asany.example.*.dao",
    },
    repositoryBaseClass = ComplexJpaRepository.class)
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, QuartzAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(Application.class);
  }

  @Bean
  public ShardingStrategyCustomizer shardingStrategyCustomizer() {
    return conf -> {
      Collection<TableRuleConfiguration> tableRuleConfigs = conf.getTableRuleConfigs();
      // table rule
      TableRuleConfiguration table2Rule = new TableRuleConfiguration("sys_user");

      MyDatabaseShardingAlgorithm myDatabaseShardingAlgorithm = new MyDatabaseShardingAlgorithm();
      ShardingStrategyConfiguration shardingStrategyConfiguration =
          new StandardShardingStrategyConfiguration("username", myDatabaseShardingAlgorithm);
      table2Rule.setDatabaseShardingStrategyConfig(shardingStrategyConfiguration);
      tableRuleConfigs.add(table2Rule);
      // key生成规则
      //    KeyGeneratorConfiguration key2Gen = new KeyGeneratorConfiguration("PUSHINFODETAIL",
      // "id");
      //    table2Rule.setKeyGeneratorConfig(key2Gen);

      StandardShardingStrategyConfiguration databaseShardingStrategy =
          new StandardShardingStrategyConfiguration("username", myDatabaseShardingAlgorithm);

      // 分表策略
      //      ShardingStrategyConfiguration tableSharding2StrategyConfig = new
      // InlineShardingStrategyConfiguration("info_type", "t_wx_push_info_details$->{info_type % 8 +
      // 1}");
      //      table2Rule.setTableShardingStrategyConfig(databaseShardingStrategy);
      //      tableRuleConfigs.add(table2Rule);

      conf.setDefaultDatabaseShardingStrategyConfig(databaseShardingStrategy);
    };
  }

  @Bean
  public MultiDataSourceManager dataSourceManager() {
    Map xxx = new HashMap() {};

    //    return new MultiDataSourceManager() {
    //
    //      @Override
    //      public DataSource getDataSource(String dataSourceKey) {
    //        return (DataSource) xxx.get(dataSourceKey);
    //      }
    //
    //      @Override
    //      public Map getAllDataSources() {
    //        return xxx;
    //      }
    //    };

    return new AbstractMultiDataSourceManager(new HashMap<>());
  }
}
