package org.jfantasy.graphql.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.graphql.client.GraphQLTemplate;
import org.jfantasy.graphql.gateway.GraphQLTemplateFactory;
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
    this.objectMapper = objectMapper;
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
}
