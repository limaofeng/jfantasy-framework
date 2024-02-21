package net.asany.jfantasy.framework.security.authorization.policy.config;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class PolicyPropertyUtils extends PropertyUtils {
  @Override
  public Property getProperty(Class<?> type, String name) {
    if (type == Configuration.class && "default".equals(name)) {
      name = "defaultPolicy";
    }
    return super.getProperty(type, name);
  }
}
