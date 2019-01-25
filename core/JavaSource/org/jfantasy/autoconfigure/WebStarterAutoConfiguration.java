package org.jfantasy.autoconfigure;

import org.jfantasy.framework.spring.config.WebMvcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ComponentScan({"org.jfantasy.framework.spring.mvc.http"})
@Import({WebMvcConfig.class})
public class WebStarterAutoConfiguration {

}
