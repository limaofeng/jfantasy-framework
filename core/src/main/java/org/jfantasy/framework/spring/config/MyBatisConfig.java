package org.jfantasy.framework.spring.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.jfantasy.autoconfigure.properties.SequenceProperties;
import org.jfantasy.framework.dao.Page;
import org.jfantasy.framework.dao.mybatis.ConfigurationPropertiesCustomizer;
import org.jfantasy.framework.dao.mybatis.binding.MyBatisMapperRegistry;
import org.jfantasy.framework.dao.mybatis.dialect.MySQLDialect;
import org.jfantasy.framework.dao.mybatis.interceptors.AutoKeyInterceptor;
import org.jfantasy.framework.dao.mybatis.interceptors.LimitInterceptor;
import org.jfantasy.framework.dao.mybatis.interceptors.MultiDataSourceInterceptor;
import org.jfantasy.framework.dao.mybatis.keygen.bean.Sequence;
import org.jfantasy.framework.dao.mybatis.keygen.util.DataBaseKeyGenerator;
import org.jfantasy.framework.dao.mybatis.sqlmapper.SqlMapper;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.PropertiesHelper;
import org.jfantasy.framework.util.common.StringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EntityScan("org.jfantasy.framework.dao.mybatis.keygen.bean")
@ComponentScan("org.jfantasy.framework.dao.mybatis.keygen")
@MapperScan(
    markerInterface = SqlMapper.class,
    basePackages = "org.jfantasy.framework.dao.mybatis.keygen.dao")
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@EnableConfigurationProperties({MybatisProperties.class, SequenceProperties.class})
public class MyBatisConfig {

  private final HikariDataSource dataSource;

  @SneakyThrows
  @Autowired
  public MyBatisConfig(
      DataSourceProperties dataSourceProperties,
      MybatisProperties mybatisProperties,
      ObjectProvider<List<ConfigurationPropertiesCustomizer>> configurationCustomizersProvider) {

    List<ConfigurationPropertiesCustomizer> configurationPropertiesCustomizers =
        configurationCustomizersProvider.getIfAvailable();

    Properties properties = mybatisProperties.getConfigurationProperties();
    if (properties == null) {
      mybatisProperties.setConfigurationProperties(properties = new Properties());
    }

    if (configurationPropertiesCustomizers != null) {
      for (ConfigurationPropertiesCustomizer customizer : configurationPropertiesCustomizers) {
        customizer.apply(properties);
      }
    }

    String[] mapperLocations = mybatisProperties.getMapperLocations();
    PropertiesHelper helper = PropertiesHelper.load("plugin.properties");
    String[] extMapperLocations = helper.getMergeProperty("mybatis.mapper-locations");
    for (String location : extMapperLocations) {
      mapperLocations =
          ObjectUtil.merge(mapperLocations, StringUtil.tokenizeToStringArray(location, ","));
    }
    mybatisProperties.setMapperLocations(mapperLocations);

    String[] typeAliasesPackage = helper.getMergeProperty("mybatis.type-aliases-package");

    if (StringUtil.isNotBlank(mybatisProperties.getTypeAliasesPackage())) {
      typeAliasesPackage =
          ObjectUtil.join(typeAliasesPackage, mybatisProperties.getTypeAliasesPackage());
    }

    mybatisProperties.setTypeAliasesPackage(StringUtil.join(typeAliasesPackage, ","));

    // Mybatis DataSource
    this.dataSource =
        dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    this.dataSource.setPoolName("hikari-myBatis");
  }

  public DataSource getMyBatisDataSource() {
    return this.dataSource;
  }

  @Bean("asany.sqlSessionFactoryBeanCustomizer")
  public SqlSessionFactoryBeanCustomizer sqlSessionFactoryBeanCustomizer() {
    return factoryBean -> factoryBean.setDataSource(MyBatisConfig.this.dataSource);
  }

  @Bean(name = "dataSourceTransactionManager")
  public PlatformTransactionManager dataSourceTransactionManager() {
    DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
    dataSourceTransactionManager.setDataSource(this.dataSource);
    return dataSourceTransactionManager;
  }

  @Bean
  public DataBaseKeyGenerator dataBaseKeyGenerator(SequenceProperties properties) {
    return new DataBaseKeyGenerator(properties.getPoolSize());
  }

  @Bean("asany.mybatisConfigurationCustomizer")
  public ConfigurationCustomizer mybatisConfigurationCustomizer() {
    return configuration -> {
      ClassUtil.setFieldValue(
          configuration, "mapperRegistry", new MyBatisMapperRegistry(configuration));

      configuration.setCacheEnabled(false);
      configuration.setLazyLoadingEnabled(true);
      configuration.setAggressiveLazyLoading(false);

      Properties settings = new Properties();
      settings.setProperty(
          "dialectClass", "org.jfantasy.framework.dao.mybatis.dialect.MySQLDialect");
      configuration.setVariables(settings);

      configuration.addInterceptor(new MultiDataSourceInterceptor());
      configuration.addInterceptor(new AutoKeyInterceptor());
      configuration.addInterceptor(new LimitInterceptor(MySQLDialect.class));

      TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
      typeAliasRegistry.registerAlias(Page.class);
      typeAliasRegistry.registerAlias(Sequence.class);
    };
  }
}
