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
package net.asany.jfantasy.graphql.gateway.service;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;
import net.asany.jfantasy.graphql.client.GraphQLTemplate;
import net.asany.jfantasy.graphql.client.GraphQLWebSocketClient;
import net.asany.jfantasy.graphql.gateway.GraphQLClient;
import net.asany.jfantasy.graphql.gateway.GraphQLClientFactory;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;
import net.asany.jfantasy.graphql.gateway.util.GraphQLUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class DefaultGraphQLClientFactory implements GraphQLClientFactory {

  private final ResourceLoader resourceLoader;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public DefaultGraphQLClientFactory(
      ResourceLoader resourceLoader, RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.resourceLoader = resourceLoader;
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper.copy();
  }

  @Override
  @SneakyThrows
  public GraphQLClient client(GatewayConfig.ServiceConfig config) {
    GraphQLTemplate graphQLTemplate =
        new GraphQLTemplate(
            this.resourceLoader, this.restTemplate, config.getUrl(), this.objectMapper);
    if (config.getHeaders() != null) {
      HttpHeaders headers = new HttpHeaders();
      config.getHeaders().forEach(headers::add);
      graphQLTemplate.setDefaultHeaders(headers);
    }

    return DefaultGraphQLClient.builder()
        .graphQLTemplate(graphQLTemplate)
        .graphQLWebSocketClient(
            new GraphQLWebSocketClient(
                GraphQLUtils.convertToWebSocketUrl(
                    config.getUrl(), config.getSubscriptions().getPath())))
        .introspectionHeaders(config.getIntrospection().getHeaders())
        .build();
  }

  public <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
    SimpleModule module = new SimpleModule();
    module.addSerializer(type, ser);
    this.objectMapper.registerModule(module);
  }
}
