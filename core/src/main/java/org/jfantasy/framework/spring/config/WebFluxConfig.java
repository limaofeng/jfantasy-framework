package org.jfantasy.framework.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.spring.mvc.reactive.WebFluxResponseBodyResultHandler;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.web.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@Order(value = WebFluxConfig.ORDER)
@ComponentScan({"org.jfantasy.framework.spring.mvc.reactive"})
public class WebFluxConfig implements WebFluxConfigurer {

  public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 32;

  private final ApplicationContext applicationContext;

  private final ObjectMapper objectMapper;

  @Autowired
  public WebFluxConfig(ApplicationContext applicationContext, ObjectMapper objectMapper) {
    this.applicationContext = applicationContext;
    this.objectMapper = objectMapper;
  }

  @Override
  public Validator getValidator() {
    return BaseConfig.getValidator(applicationContext);
  }

  @Override
  public void addCorsMappings(@SuppressWarnings("NullableProblems") CorsRegistry registry) {
    String path = "/**";

    boolean credentials = true;
    String originPatterns = ServletUtils.CORS_DEFAULT_ORIGIN_PATTERNS;
    String[] allowedHeaders = ServletUtils.CORS_DEFAULT_ALLOWED_HEADERS;
    String[] allowedMethods = ServletUtils.CORS_DEFAULT_ALLOWED_METHODS;
    String[] exposeHeaders = ServletUtils.CORS_DEFAULT_EXPOSE_METHODS;
    long maxAge = ServletUtils.CORS_DEFAULT_MAX_AGE;

    if (SpringBeanUtils.containsBean(CorsWebFilter.class)) {
      CorsWebFilter corsFilter = SpringBeanUtils.getBean(CorsWebFilter.class);
      UrlBasedCorsConfigurationSource configSource = ClassUtil.getValue(corsFilter, "configSource");

      CorsConfiguration corsConfiguration = new CorsConfiguration();
      corsConfiguration.addAllowedOriginPattern(originPatterns);
      corsConfiguration.setAllowedHeaders(Arrays.asList(allowedHeaders));
      corsConfiguration.setAllowedMethods(Arrays.asList(allowedMethods));
      corsConfiguration.setExposedHeaders(Arrays.asList(exposeHeaders));
      corsConfiguration.setAllowCredentials(credentials);
      corsConfiguration.setMaxAge(maxAge);

      configSource.registerCorsConfiguration(path, corsConfiguration);
    } else {
      registry
          .addMapping(path)
          .allowedOriginPatterns(originPatterns)
          .allowedMethods(allowedMethods)
          .allowedHeaders(allowedHeaders)
          .exposedHeaders(exposeHeaders)
          .allowCredentials(credentials)
          .maxAge(maxAge);
    }
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter =
        new MappingJackson2HttpMessageConverter(this.objectMapper);
    converter.setSupportedMediaTypes(
        Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
    return converter;
  }

  @Bean
  public WebFluxResponseBodyResultHandler webFluxResponseBodyResultHandler(
      ServerCodecConfigurer serverCodecConfigurer,
      RequestedContentTypeResolver requestedContentTypeResolver) {
    return new WebFluxResponseBodyResultHandler(
        serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
  }
}
