package org.jfantasy.autoconfigure;

import org.jfantasy.autoconfigure.properties.DataSourceProxyProperties;
import org.jfantasy.framework.dao.DatasourceProxyBeanPostProcessor;
import org.jfantasy.framework.dao.hibernate.InterceptorRegistration;
import org.jfantasy.framework.dao.hibernate.interceptors.BusEntityInterceptor;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.spring.config.AppConfig;
import org.jfantasy.framework.spring.config.DaoConfig;
import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
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
@EnableConfigurationProperties(DataSourceProxyProperties.class)
public class CoreAutoConfiguration {

  @Bean
  public SpringBeanUtils springBeanUtils() {
    return new SpringBeanUtils();
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
