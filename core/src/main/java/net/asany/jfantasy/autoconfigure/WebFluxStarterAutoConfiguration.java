package net.asany.jfantasy.autoconfigure;

import net.asany.jfantasy.framework.spring.config.WebFluxConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

@ConditionalOnClass(EnableWebFlux.class)
@Configuration
@Import(WebFluxConfig.class)
@ComponentScan({"net.asany.jfantasy.framework.spring.mvc.reactive"})
public class WebFluxStarterAutoConfiguration {}
