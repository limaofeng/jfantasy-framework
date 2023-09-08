package org.jfantasy.autoconfigure;

import org.jfantasy.framework.spring.config.WebFluxConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

@ConditionalOnClass(EnableWebFlux.class)
@Configuration
@Import(WebFluxConfig.class)
public class WebFluxStarterAutoConfiguration {}
