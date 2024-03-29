package org.jfantasy.autoconfigure;

import org.jfantasy.autoconfigure.properties.CacheAdvanceProperties;
import org.jfantasy.autoconfigure.properties.DataSourceExtendedProperties;
import org.jfantasy.framework.dao.datasource.DatasourceProxyBeanPostProcessor;
import org.jfantasy.framework.dao.hibernate.InterceptorRegistration;
import org.jfantasy.framework.dao.hibernate.interceptors.BusEntityInterceptor;
import org.jfantasy.framework.spring.CacheBeanPostProcessor;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.spring.config.AppConfig;
import org.jfantasy.framework.spring.config.DaoConfig;
import org.jfantasy.framework.util.common.StringUtil;
import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 核心配置类
 *
 * @author limaofeng
 */
@Configuration
@AutoConfigureAfter({
  DataSourceAutoConfiguration.class,
  MybatisLanguageDriverAutoConfiguration.class
})
@Import({AppConfig.class, DaoConfig.class})
@EnableConfigurationProperties({
  DataSourceExtendedProperties.class,
  DataSourceProperties.class,
  CacheAdvanceProperties.class
})
public class CoreAutoConfiguration {

  @Bean
  public SpringBeanUtils springBeanUtils() {
    return new SpringBeanUtils();
  }

  @Bean
  public CacheBeanPostProcessor cacheBeanPostProcessor(CacheAdvanceProperties cacheProperties) {
    return new CacheBeanPostProcessor(
        StringUtil.tokenizeToStringArray(cacheProperties.getBeanNames()));
  }

  @Bean
  public BusEntityInterceptor busEntityInterceptor() {
    return new BusEntityInterceptor();
  }

  @Bean("hibernate.InterceptorRegistration")
  public InterceptorRegistration interceptorRegistration() {
    return new InterceptorRegistration(busEntityInterceptor());
  }

  @Bean
  @ConditionalOnExpression("${spring.datasource.proxy:false}")
  public DatasourceProxyBeanPostProcessor datasourceProxyBeanPostProcessor() {
    return new DatasourceProxyBeanPostProcessor();
  }
}
