package net.asany.jfantasy.graphql.gateway.config;

import java.util.List;
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
    private String mapping;
    private Boolean exclude;
    private String dataFetcher;
    private List<ArgumentConfig> arguments;
  }

  @Data
  public static class ArgumentConfig {
    private String name;
    private String mapping;
    private Boolean exclude;
  }
}
