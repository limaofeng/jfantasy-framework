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

import graphql.introspection.IntrospectionQueryBuilder;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.client.GraphQLResponse;
import net.asany.jfantasy.graphql.client.GraphQLTemplate;
import net.asany.jfantasy.graphql.client.GraphQLWebSocketClient;
import net.asany.jfantasy.graphql.client.QueryPayload;
import net.asany.jfantasy.graphql.gateway.GraphQLClient;
import net.asany.jfantasy.graphql.gateway.data.BridgePublisher;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;

@Slf4j
@Builder
public class DefaultGraphQLClient implements GraphQLClient {

  private GraphQLTemplate graphQLTemplate;
  private GraphQLWebSocketClient graphQLWebSocketClient;
  private Map<String, String> introspectionHeaders;

  /** 按照 token 缓存 webSocketClient */
  @Builder.Default private Map<String, GraphQLWebSocketClient> webSocketClients = new HashMap<>();

  @Override
  public Document introspectionQuery() throws IOException {
    GraphQLTemplate introspectionTemplate = graphQLTemplate;
    if (this.introspectionHeaders != null) {
      HttpHeaders headers = new HttpHeaders();
      this.introspectionHeaders.forEach(headers::add);
      introspectionTemplate = graphQLTemplate.withHeaders(headers);
    }

    String introspectionQuery =
        IntrospectionQueryBuilder.build(
            IntrospectionQueryBuilder.Options.defaultOptions()
                .inputValueDeprecation(false)
                .isOneOf(false));

    GraphQLResponse response = introspectionTemplate.post(introspectionQuery, "IntrospectionQuery");

    //noinspection unchecked
    Map<String, Object> introspectionResult = response.get("$.data", HashMap.class);
    return new IntrospectionResultToSchema().createSchemaDefinition(introspectionResult);
  }

  @Override
  public GraphQLResponse query(QueryPayload payload, String token) throws IOException {
    GraphQLTemplate qlTemplate = graphQLTemplate;
    if (token != null) {
      qlTemplate = graphQLTemplate.withBearerAuth(token);
    }
    return qlTemplate.post(payload.getQuery(), payload.getOperationName(), payload.getVariables());
  }

  @Override
  @SneakyThrows
  public Publisher<Object> subscribe(
      QueryPayload payload, String token, Function<Object, Object> converter) {
    GraphQLWebSocketClient webSocketClient = this.graphQLWebSocketClient;

    if (token != null) {
      webSocketClient =
          webSocketClients.computeIfAbsent(
              token, key -> graphQLWebSocketClient.withBearerAuth(token));
      webSocketClient.connectBlocking(10, TimeUnit.SECONDS);
    }

    BridgePublisher publisher = new BridgePublisher();
    Runnable unsubscribe =
        webSocketClient.subscribe(
            payload,
            message -> {
              try {
                publisher.emit(converter.apply(message));
              } catch (Exception e) {
                publisher.error(e);
              }
            });

    // 当订阅关闭时，取消订阅
    publisher.onDispose(unsubscribe);

    // 当订阅关闭时，检查是否还有其他订阅，没有则关闭连接
    if (token != null) {
      publisher.onDispose(
          () -> {
            GraphQLWebSocketClient userWebSocketClient = webSocketClients.get(token);
            if (userWebSocketClient.activeSubscriptions() == 0) {
              webSocketClients.remove(token);
              userWebSocketClient.close();
            }
          });
    }

    return publisher;
  }

  @Override
  public void connect() {
    this.graphQLWebSocketClient.connect();
  }
}
