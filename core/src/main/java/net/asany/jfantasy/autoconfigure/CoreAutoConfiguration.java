package net.asany.jfantasy.autoconfigure;

import net.asany.jfantasy.autoconfigure.properties.CacheAdvanceProperties;
import net.asany.jfantasy.autoconfigure.properties.DataSourceExtendedProperties;
import net.asany.jfantasy.framework.dao.datasource.DatasourceProxyBeanPostProcessor;
import net.asany.jfantasy.framework.dao.hibernate.InterceptorRegistration;
import net.asany.jfantasy.framework.dao.hibernate.interceptors.BusEntityInterceptor;
import net.asany.jfantasy.framework.spring.CacheBeanPostProcessor;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.spring.config.AppConfig;
import net.asany.jfantasy.framework.spring.config.DaoConfig;
import net.asany.jfantasy.framework.spring.config.JacksonConfig;
import net.asany.jfantasy.framework.util.common.StringUtil;
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
@Import({AppConfig.class, DaoConfig.class, JacksonConfig.class})
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
