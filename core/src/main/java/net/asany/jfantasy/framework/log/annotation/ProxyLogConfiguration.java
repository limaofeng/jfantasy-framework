package net.asany.jfantasy.framework.log.annotation;

import net.asany.jfantasy.framework.log.interceptor.LogInterceptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * 代理日志配置
 *
 * @author limaofeng
 */
@Configuration
class ProxyLogConfiguration extends AbstractLogConfiguration {

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public LogInterceptor logInterceptor() {
    return new LogInterceptor();
  }
}
