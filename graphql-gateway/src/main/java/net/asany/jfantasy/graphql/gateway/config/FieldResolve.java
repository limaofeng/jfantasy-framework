package net.asany.jfantasy.graphql.gateway.config;

import graphql.language.Type;
import graphql.language.Value;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder")
public class FieldResolve {
  private String query;
  private Map<String, FieldArgument> arguments;

  @Data
  @lombok.Builder(builderClassName = "Builder")
  public static class FieldArgument {
    private String name;
    private Type<?> type;
    private boolean reference;
    private String sourceValue;
    private Value<?> value;

    public <T extends Value<?>> T getValue(Class<T> clazz) {
      return clazz.cast(value);
    }
  }
}
