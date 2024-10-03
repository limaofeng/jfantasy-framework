/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.spring.mvc.servlet.method.PagerModelAttributeMethodProcessor;
import net.asany.jfantasy.framework.spring.mvc.servlet.method.PropertyFilterModelAttributeMethodProcessor;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.web.ServletUtils;
import net.asany.jfantasy.framework.web.filter.ConversionCharacterEncodingFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Web 配置
 *
 * @author limaofeng
 */
@Configuration
@Order(value = WebMvcConfig.ORDER)
public class WebMvcConfig implements WebMvcConfigurer {

  public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 32;

  private final ApplicationContext applicationContext;

  private final ObjectMapper objectMapper;

  @Autowired
  public WebMvcConfig(ApplicationContext applicationContext, ObjectMapper objectMapper) {
    this.applicationContext = applicationContext;
    this.objectMapper = objectMapper;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("*.html").addResourceLocations("/");
  }

  @Bean
  public LocaleResolver localeResolver() {
    return new CookieLocaleResolver();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    registry.addInterceptor(localeChangeInterceptor);
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    Class<?>[] removeClazz =
        new Class[] {StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class};
    converters.removeIf(converter -> ObjectUtil.exists(removeClazz, converter.getClass()));
    converters.add(0, new MappingJackson2HttpMessageConverter(this.objectMapper));
    converters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new PropertyFilterModelAttributeMethodProcessor());
    argumentResolvers.add(new PagerModelAttributeMethodProcessor());
  }

  @Override
  public Validator getValidator() {
    return BaseConfig.getValidator(applicationContext);
  }

  @Override
  public void addCorsMappings(@NotNull CorsRegistry registry) {
    //noinspection DuplicatedCode
    String path = "/**";

    boolean credentials = true;
    String originPatterns = ServletUtils.CORS_DEFAULT_ORIGIN_PATTERNS;
    String[] allowedHeaders = ServletUtils.CORS_DEFAULT_ALLOWED_HEADERS;
    String[] allowedMethods = ServletUtils.CORS_DEFAULT_ALLOWED_METHODS;
    String[] exposeHeaders = ServletUtils.CORS_DEFAULT_EXPOSE_METHODS;
    long maxAge = ServletUtils.CORS_DEFAULT_MAX_AGE;

    if (SpringBeanUtils.containsBean(CorsFilter.class)) {
      CorsFilter corsFilter = SpringBeanUtils.getBean(CorsFilter.class);
      //noinspection DuplicatedCode
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
  public FilterRegistrationBean<ConversionCharacterEncodingFilter>
      conversionCharacterEncodingFilter() {
    FilterRegistrationBean<ConversionCharacterEncodingFilter> filterRegistrationBean =
        new FilterRegistrationBean<>();
    filterRegistrationBean.setFilter(new ConversionCharacterEncodingFilter());
    filterRegistrationBean.setEnabled(true);
    filterRegistrationBean.setOrder(200);
    filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
    filterRegistrationBean.addUrlPatterns("/*");
    return filterRegistrationBean;
  }
}
