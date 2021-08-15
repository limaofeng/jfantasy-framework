package org.jfantasy.autoconfigure;

import org.jfantasy.framework.dao.DataSourceSetUtf8mb4;
import org.jfantasy.framework.dao.hibernate.InterceptorRegistration;
import org.jfantasy.framework.dao.hibernate.interceptors.BusEntityInterceptor;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.spring.config.AppConfig;
import org.jfantasy.framework.spring.config.DaoConfig;
import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 核心配置类
 *
 * @author limaofeng
 */
@Configuration
@AutoConfigureAfter({  DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class })
@Import({AppConfig.class, DaoConfig.class})
public class CoreAutoConfiguration {

  @Bean
  public SpringContextUtil springContextUtil() {
    return new SpringContextUtil();
  }

  @Bean
  public DataSourceSetUtf8mb4 dataSourceSetUtf8mb4() {
    return new DataSourceSetUtf8mb4();
  }

  @Bean
  public BusEntityInterceptor busEntityInterceptor() {
    return new BusEntityInterceptor();
  }

  @Bean("hibernate.InterceptorRegistration")
  public InterceptorRegistration interceptorRegistration() {
    return new InterceptorRegistration(busEntityInterceptor());
  }
}
