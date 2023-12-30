package net.asany.jfantasy.graphql.gateway.config;

import graphql.language.Type;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;
import org.jetbrains.annotations.NotNull;

@Data
@Builder(builderClassName = "Builder")
public class SchemaOverrideField {
  private String name;
  private Type<?> type;
  private Type<?> originalType;
  private String mapping;
  private boolean exclude;
  private boolean extended;
  private String dataFetcher;
  private FieldResolve resolve;

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

  public boolean isTypeChanged() {
    return this.type != null;
  }

  public boolean isTypeChanged(@NotNull Type<?> type) {
    if (this.type == null) {
      return false;
    }
    return !GraphQLTypeUtils.getTypeSource(type).equals(GraphQLTypeUtils.getTypeSource(this.type));
  }

  public String getAlias(String alias) {
    if (alias != null) {
      return alias;
    }
    return isNameChanged() ? this.name : null;
  }

  public boolean hasIncludeArgument(String name) {
    return this.arguments.containsKey(name);
  }

  public static class Builder {

    Builder() {
      this.arguments = new HashMap<>();
    }

    public Builder excludeArgument(String name) {
      SchemaOverrideFieldArgument argument =
          arguments.computeIfAbsent(
              name, k -> SchemaOverrideFieldArgument.builder().name(name).build());
      argument.setExclude(true);
      return this;
    }

    public Builder type(String type) {
      this.type = GraphQLTypeUtils.parseType(type);
      return this;
    }

    public Builder resolve(String resolve) {
      this.resolve =
          FieldResolve.builder().query("user").arguments(Map.of("id", "createdBy")).build();
      ;
      return this;
    }

    public Builder excludeArgument(String name, String value) {
      arguments.put(name, SchemaOverrideFieldArgument.builder().name(name).value(value).build());
      return this;
    }

    public Builder argument(String name, String mapping, String defaultValue) {
      arguments.put(
          name,
          SchemaOverrideFieldArgument.builder()
              .name(name)
              .mapping(mapping)
              .defaultValue(defaultValue)
              .build());
      return this;
    }
  }
}
