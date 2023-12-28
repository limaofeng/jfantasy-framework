package net.asany.jfantasy.graphql.gateway.config;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class GatewayPropertyUtils extends PropertyUtils {
  @Override
  public Property getProperty(Class<?> type, String name) {
    if (type == DataFetcherConfig.class && "class".equals(name)) {
      name = "className";
    }
    return super.getProperty(type, name);
  }
}
