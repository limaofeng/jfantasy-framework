package net.asany.jfantasy.framework.security.authorization.policy.config;

import java.util.List;
import lombok.Data;
import net.asany.jfantasy.framework.security.authorization.policy.Role;

@Data
public class ConfigRole implements Role {
  private String id;
  private String description;
  private List<String> permissions;
}
