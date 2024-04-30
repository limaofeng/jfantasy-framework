package net.asany.jfantasy.graphql.gateway.service;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.graphql.client.GraphQLTemplate;
import net.asany.jfantasy.graphql.gateway.GraphQLTemplateFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class DefaultGraphQLTemplateFactory implements GraphQLTemplateFactory {

  private final ResourceLoader resourceLoader;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public DefaultGraphQLTemplateFactory() {
    this(
        new DefaultResourceLoader(),
        new RestTemplateBuilder().build(),
        JSON.initialize().getObjectMapper());
  }

  public DefaultGraphQLTemplateFactory(
      ResourceLoader resourceLoader, RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.resourceLoader = resourceLoader;
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper.copy();
  }

  @Override
  public GraphQLTemplate client(RemoteGraphQLService service) {
    GraphQLTemplate client =
        new GraphQLTemplate(
            this.resourceLoader, this.restTemplate, service.getUrl(), this.objectMapper);
    if (service.getHeaders() != null) {
      HttpHeaders headers = new HttpHeaders();
      service.getHeaders().forEach(headers::add);
      client.setDefaultHeaders(headers);
    }
    return client;
  }

  public <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
    SimpleModule module = new SimpleModule();
    module.addSerializer(type, ser);
    this.objectMapper.registerModule(module);
  }
}
