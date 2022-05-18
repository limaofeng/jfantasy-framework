package org.jfantasy.framework.spring.config;

import com.alibaba.druid.pool.DruidDataSource;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.jfantasy.framework.dao.Pagination;
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
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.*;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Configuration
@EntityScan("org.jfantasy.framework.dao.mybatis.keygen.bean")
@ComponentScan("org.jfantasy.framework.dao.mybatis.keygen")
@MapperScan(
    markerInterface = SqlMapper.class,
    basePackages = "org.jfantasy.framework.dao.mybatis.keygen.dao")
@EnableConfigurationProperties(MybatisProperties.class)
public class MyBatisConfig {

  private final DruidDataSource dataSource;

  private final MybatisProperties properties;

  private final Interceptor[] interceptors;

  private final TypeHandler[] typeHandlers;

  private final LanguageDriver[] languageDrivers;

  private final ResourceLoader resourceLoader;

  private final DatabaseIdProvider databaseIdProvider;

  private final List<ConfigurationCustomizer> configurationCustomizers;

  @SneakyThrows
  @Autowired
  public MyBatisConfig(
      DataSource dataSource,
      MybatisProperties properties,
      ObjectProvider<Interceptor[]> interceptorsProvider,
      ObjectProvider<TypeHandler[]> typeHandlersProvider,
      ObjectProvider<LanguageDriver[]> languageDriversProvider,
      ResourceLoader resourceLoader,
      ObjectProvider<DatabaseIdProvider> databaseIdProvider,
      ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {

    PropertiesHelper helper = PropertiesHelper.load("plugin.properties");
    properties.setMapperLocations(
        ObjectUtil.merge(
            properties.getMapperLocations(), helper.getMergeProperty("mybatis.mapper-locations")));

    this.properties = properties;
    this.interceptors = interceptorsProvider.getIfAvailable();
    this.typeHandlers = typeHandlersProvider.getIfAvailable();
    this.languageDrivers = languageDriversProvider.getIfAvailable();
    this.resourceLoader = resourceLoader;
    this.databaseIdProvider = databaseIdProvider.getIfAvailable();
    this.configurationCustomizers =
        ObjectUtil.defaultValue(configurationCustomizersProvider.getIfAvailable(), ArrayList::new);
    this.configurationCustomizers.add(0, mybatisConfigurationCustomizer());

    // Mybatis DataSource
    this.dataSource = ((DruidDataSource) dataSource).cloneDruidDataSource();
    this.dataSource.init();
  }

  @PreDestroy
  public void destroy() {
    this.dataSource.close();
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
    factory.setDataSource(this.dataSource);
    factory.setVfs(SpringBootVFS.class);
    if (StringUtils.hasText(this.properties.getConfigLocation())) {
      factory.setConfigLocation(
          this.resourceLoader.getResource(this.properties.getConfigLocation()));
    }
    applyConfiguration(factory);
    if (this.properties.getConfigurationProperties() != null) {
      factory.setConfigurationProperties(this.properties.getConfigurationProperties());
    }
    if (!ObjectUtils.isEmpty(this.interceptors)) {
      factory.setPlugins(this.interceptors);
    }
    if (this.databaseIdProvider != null) {
      factory.setDatabaseIdProvider(this.databaseIdProvider);
    }
    if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
      factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
    }
    if (this.properties.getTypeAliasesSuperType() != null) {
      factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
    }
    if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
      factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
    }
    if (!ObjectUtils.isEmpty(this.typeHandlers)) {
      factory.setTypeHandlers(this.typeHandlers);
    }
    if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
      factory.setMapperLocations(this.properties.resolveMapperLocations());
    }
    Set<String> factoryPropertyNames =
        Stream.of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors())
            .map(PropertyDescriptor::getName)
            .collect(Collectors.toSet());
    Class<? extends LanguageDriver> defaultLanguageDriver =
        this.properties.getDefaultScriptingLanguageDriver();
    if (factoryPropertyNames.contains("scriptingLanguageDrivers")
        && !ObjectUtils.isEmpty(this.languageDrivers)) {
      // Need to mybatis-spring 2.0.2+
      factory.setScriptingLanguageDrivers(this.languageDrivers);
      if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
        defaultLanguageDriver = this.languageDrivers[0].getClass();
      }
    }
    if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
      // Need to mybatis-spring 2.0.2+
      factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
    }
    return factory.getObject();
  }

  @Bean(name = "dataSourceTransactionManager")
  public PlatformTransactionManager dataSourceTransactionManager() {
    DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
    dataSourceTransactionManager.setDataSource(this.dataSource);
    return dataSourceTransactionManager;
  }

  @Bean
  public DataBaseKeyGenerator dataBaseKeyGenerator(
      @Value("${spring.jfantasy.sequence.pool-size:10}") Integer poolSize) {
    return new DataBaseKeyGenerator(poolSize);
  }

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
      typeAliasRegistry.registerAlias(Pagination.class);
      typeAliasRegistry.registerAlias(Sequence.class);
    };
  }

  private void applyConfiguration(SqlSessionFactoryBean factory) {
    org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
    if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
      configuration = new org.apache.ibatis.session.Configuration();
    }
    if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
      for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
        customizer.customize(configuration);
      }
    }
    factory.setConfiguration(configuration);
  }
}
