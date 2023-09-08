package org.jfantasy.framework.spring.config;

import java.util.Arrays;
import org.hibernate.validator.HibernateValidator;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.web.ServletUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.config.CorsRegistry;

public class BaseConfig {

  public static void addCorsMappings(CorsRegistry registry) {
    String path = "/**";

    boolean credentials = true;
    String originPatterns = ServletUtils.CORS_DEFAULT_ORIGIN_PATTERNS;
    String[] allowedHeaders = ServletUtils.CORS_DEFAULT_ALLOWED_HEADERS;
    String[] allowedMethods = ServletUtils.CORS_DEFAULT_ALLOWED_METHODS;
    String[] exposeHeaders = ServletUtils.CORS_DEFAULT_EXPOSE_METHODS;
    long maxAge = ServletUtils.CORS_DEFAULT_MAX_AGE;

    if (SpringBeanUtils.containsBean(CorsFilter.class)) {
      CorsFilter corsFilter = SpringBeanUtils.getBean(CorsFilter.class);
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

  public static Validator getValidator(ApplicationContext applicationContext) {
    if (applicationContext instanceof XmlWebApplicationContext) {
      ConfigurableApplicationContext configurableApplicationContext =
          (ConfigurableApplicationContext) applicationContext;
      DefaultListableBeanFactory defaultListableBeanFactory =
          (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
      BeanDefinitionBuilder beanDefinitionBuilder =
          BeanDefinitionBuilder.genericBeanDefinition(LocalValidatorFactoryBean.class);
      beanDefinitionBuilder.setAutowireMode(2);
      beanDefinitionBuilder.addPropertyValue("providerClass", HibernateValidator.class);
      defaultListableBeanFactory.registerBeanDefinition(
          "validator", beanDefinitionBuilder.getBeanDefinition());
      return configurableApplicationContext.getBean("validator", Validator.class);
    }
    return null;
  }
}
