package org.jfantasy.graphql.gateway.config;

import java.util.List;
import lombok.Data;

@Data
public class GatewayConfig {
  private List<ServiceConfig> services;

  @Data
  public static class ServiceConfig {
    private String name;
    private String url;
    private List<OverrideConfig> override;
  }

  @Data
  public static class OverrideConfig {
    private String type;
    private String rename;
    private List<FieldConfig> fields;
  }

  @Data
  public static class FieldConfig {
    private String name;
    private String rename;
    private Boolean ignore;
    private List<ArgumentConfig> arguments;
  }

  @Data
  public static class ArgumentConfig {
    private String name;
    private String rename;
  }
}
