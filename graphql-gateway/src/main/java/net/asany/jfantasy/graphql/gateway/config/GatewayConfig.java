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
package net.asany.jfantasy.graphql.gateway.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GatewayConfig {
  private List<ServiceConfig> services;
  private List<ScalarConfig> scalars;
  private List<OverrideConfig> override;
  private List<DataFetcherConfig> dataFetchers;
  private List<DirectiveConfig> directives;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ServiceConfig {
    private String name;
    private String url;
    private String typeDefs;

    @Builder.Default
    private SubscriptionConfig subscriptions = SubscriptionConfig.builder().build();

    @Builder.Default
    private IntrospectionConfig introspection = IntrospectionConfig.builder().build();

    private List<String> excludeFields;
    private Map<String, String> headers;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class IntrospectionConfig {
    @Builder.Default private boolean enabled = true;
    @Builder.Default private Map<String, String> headers = new HashMap<>();
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SubscriptionConfig {
    @Builder.Default private boolean enabled = true;
    @Builder.Default private String path = "/subscriptions";
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ScalarConfig {
    private String name;
    private String description;
    private String provider;
    private String resolver;
  }

  @Data
  public static class OverrideConfig {
    private String type;
    private String mapping;
    private List<FieldConfig> fields;
  }

  @Data
  public static class FieldConfig {
    private String name;
    private String type;
    private String mapping;
    private Boolean exclude;
    private String dataFetcher;
    private String resolve;
    private List<ArgumentConfig> arguments;
  }

  @Data
  public static class ArgumentConfig {
    private String name;

    /** 映射的原始参数名 如果 mapping 未设置默认取 name。 当 mapping 在合成的 Schema 中查找失败时，默认这是一个网关添加的新字段 */
    private String mapping;

    /** 是否忽略该参数 */
    private Boolean exclude;

    /** exclude = true 时有效。 虽然已经在网关忽略了字段，但传递给真实 Schema 时，传递字段值 */
    private String value;

    /** 修改或设置 Schema 参数默认值 */
    private String defaultValue;
  }
}
