package org.jfantasy.framework.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import jakarta.annotation.PostConstruct;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.UnirestObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  private final ObjectMapper objectMapper;

  @Autowired
  public JacksonConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  public void initObjectMapper() {
    JSON.initialize(objectMapper);
    Unirest.setObjectMapper(new UnirestObjectMapper(objectMapper));
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer defaultCustomizeJackson() {
    return null;
  }
}
