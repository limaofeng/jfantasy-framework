package net.asany.jfantasy.graphql.gateway.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder")
public class SchemaOverrideField {
  private String name;
  private String mapping;
  private boolean exclude;
  private String dataFetcher;

  private Map<String, SchemaOverrideFieldArgument> arguments;

  public void renameArgument(String name, String mapping) {
    if (!this.arguments.containsKey(name)) {
      this.arguments.put(name, SchemaOverrideFieldArgument.builder().name(name).build());
    }
    SchemaOverrideFieldArgument argument = this.arguments.get(name);
    argument.setName(name);
    argument.setMapping(mapping);
  }

  public SchemaOverrideFieldArgument getArgument(String argumentName) {
    return this.arguments.get(argumentName);
  }

  public boolean isNameChanged() {
    return !this.mapping.equals(this.name);
  }

  public String getAlias(String alias) {
    if (alias != null) {
      return alias;
    }
    return isNameChanged() ? this.name : null;
  }

  public static class Builder {

    Builder() {
      this.arguments = new HashMap<>();
    }

    public Builder excludeArgument(String name) {
      if (!arguments.containsKey(name)) {
        arguments.put(name, SchemaOverrideFieldArgument.builder().name(name).build());
      }
      SchemaOverrideFieldArgument argument = arguments.get(name);
      argument.setExclude(true);
      return this;
    }

    public Builder renameArgument(String name, String mapping) {
      if (!arguments.containsKey(name)) {
        arguments.put(name, SchemaOverrideFieldArgument.builder().name(name).build());
      }
      SchemaOverrideFieldArgument argument = arguments.get(name);
      argument.setMapping(mapping);
      return this;
    }

    public void argument(String name, String mapping) {
      if (!arguments.containsKey(name)) {
        arguments.put(name, SchemaOverrideFieldArgument.builder().name(name).build());
      }
      SchemaOverrideFieldArgument argument = arguments.get(name);
      argument.setMapping(mapping);
    }
  }
}
