package net.asany.jfantasy.framework.security.authorization.config;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceActionType;

@Data
public class ConfigResource {
  private String id;
  private String service;
  private String description;
  private List<ConfigResourceAction> actions;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ConfigResourceAction implements ResourceAction {
    private String id;
    private ResourceActionType type;
    private String description;
    private List<String> operations;
    private Set<String> arn;
  }
}
