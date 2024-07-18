package net.asany.jfantasy.framework.security.authorization.config;

import net.asany.jfantasy.framework.security.authorization.policy.PermissionStatement;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class PolicyPropertyUtils extends PropertyUtils {
  @Override
  public Property getProperty(Class<?> type, String name) {
    if (type == AuthorizationConfiguration.class && "default".equals(name)) {
      name = "defaultPolicy";
    }
    if (type == AuthorizationConfiguration.class && "public-paths".equals(name)) {
      name = "publicPaths";
    }
    if (type == PermissionStatement.class && "condition".equals(name)) {
      name = "conditions";
    }
    return super.getProperty(type, name);
  }
}
