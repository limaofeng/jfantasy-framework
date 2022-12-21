package org.jfantasy.autoconfigure;

import org.jfantasy.framework.spring.config.WebMvcConfig;
import org.jfantasy.framework.web.tomcat.CustomTomcatContextCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自定义 WebStarter AutoConfiguration
 *
 * @author limaofeng
 */
@Configuration
@ComponentScan({"org.jfantasy.framework.spring.mvc.http"})
@Import({WebMvcConfig.class})
public class WebStarterAutoConfiguration {

  /**
   * 自定义 TomcatServletWebServerFactory 禁用自带的 Tomcat Session
   *
   * @return TomcatServletWebServerFactory
   */
  //  @Bean
  public CustomTomcatContextCustomizer customTomcatContextCustomizer() {
    return new CustomTomcatContextCustomizer();
  }
}
