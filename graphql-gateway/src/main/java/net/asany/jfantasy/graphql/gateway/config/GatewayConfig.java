package net.asany.jfantasy.graphql.gateway.config;

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

  @Data
  public static class ServiceConfig {
    private String name;
    private String url;
    private List<String> excludeFields;
    private Map<String, String> headers;
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
