package org.jfantasy.autoconfigure;

import org.jfantasy.framework.spring.config.WebMvcConfig;
import org.jfantasy.framework.web.tomcat.CustomTomcatContextCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 自定义 WebStarter AutoConfiguration
 *
 * @author limaofeng
 */
@ConditionalOnClass(EnableWebMvc.class)
@Configuration
@Import(WebMvcConfig.class)
public class WebStarterAutoConfiguration {

  /**
   * TODO: 禁用自带的 Tomcat Session, * 直接禁用，会导致 GraphQL 的 WebSocket 无法使用
   *
   * @return CustomTomcatContextCustomizer
   */
  public CustomTomcatContextCustomizer customTomcatContextCustomizer() {
    return new CustomTomcatContextCustomizer();
  }
}
