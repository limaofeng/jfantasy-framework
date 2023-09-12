package org.jfantasy.framework.spring.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.mashape.unirest.http.Unirest;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import org.jfantasy.framework.jackson.*;
import org.jfantasy.framework.jackson.deserializer.DateDeserializer;
import org.jfantasy.framework.jackson.serializer.DateSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Order(value = JacksonConfig.ORDER)
@Configuration
public class JacksonConfig {

  public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 30;

  @PostConstruct
  public void initObjectMapper() {
    XML.initialize();
  }

  @Bean
  public ObjectMapperBeanPostProcessor objectMapperBeanPostProcessor() {
    return new ObjectMapperBeanPostProcessor();
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer defaultCustomizeJackson() {
    return new AnyJackson2ObjectMapperBuilderCustomizer();
  }

  public static class AnyJackson2ObjectMapperBuilderCustomizer
      implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
      builder
          .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
          .serializationInclusion(JsonInclude.Include.NON_NULL)
          .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
          .featuresToEnable(
              JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, JsonParser.Feature.ALLOW_SINGLE_QUOTES)
          .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .modules(
              new SimpleModule()
                  .addSerializer(Date.class, new DateSerializer("yyyy-MM-dd HH:mm:ss"))
                  .addDeserializer(Date.class, new DateDeserializer()))
          .filters(MixInHolder.getDefaultFilterProvider());
    }
  }

  public static class AnyJackson2XmlMapperBuilderCustomizer
      implements Jackson2XmlMapperBuilderCustomizer {
    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
      JacksonXmlModule xmlModule = new JacksonXmlModule();
      builder
          .modulesToInstall(xmlModule)
          .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
          .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
  }

  public static class ObjectMapperBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
      if (bean instanceof ObjectMapper) {
        JSON.setObjectMapper((ObjectMapper) bean);
        Unirest.setObjectMapper(new UnirestObjectMapper((ObjectMapper) bean));
      }
      return bean;
    }
  }
}
