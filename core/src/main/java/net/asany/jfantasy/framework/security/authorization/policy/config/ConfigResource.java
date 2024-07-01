package net.asany.jfantasy.framework.security.authorization.policy.config;

import java.util.List;
import lombok.Data;

@Data
public class ConfigResource {
  private String id;
  private String type;
  private String description;
  private List<ConfigResourceAction> actions;

  @Data
  public static class ConfigResourceAction {
    private String id;
    private String type;
    private List<String> operations;
  }
}
