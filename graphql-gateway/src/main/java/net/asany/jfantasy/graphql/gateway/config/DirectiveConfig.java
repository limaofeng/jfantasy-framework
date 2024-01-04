package net.asany.jfantasy.graphql.gateway.config;

import graphql.introspection.Introspection;
import graphql.language.Value;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class DirectiveConfig {
  private String name;
  private String description;
  private boolean repeatable;
  private List<Introspection.DirectiveLocation> locations;
  private List<ArgumentConfig> arguments;
  private String definition;
  private String handler;

  public String getDefinition() {
    if (definition == null && name != null) {
      return DirectiveConfig.builder()
          .name(name)
          .description(description)
          .repeatable(repeatable)
          .locations(locations)
          .arguments(arguments)
          .handler(handler)
          .build()
          .getDefinition();
    }
    return definition;
  }

  public static class Builder {

    public Builder() {
      this.arguments = new ArrayList<>();
    }

    public Builder locations(Introspection.DirectiveLocation... locations) {
      this.locations = List.of(locations);
      return this;
    }

    public Builder locations(List<Introspection.DirectiveLocation> locations) {
      this.locations = locations;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder argument(String name, String type, String description) {
      ArgumentConfig.Builder builder = ArgumentConfig.builder().name(name).type(type);
      if (description != null) {
        builder.description(description);
      }
      arguments.add(builder.build());
      return this;
    }

    public Builder argument(String name, String type, String description, String defaultValue) {
      ArgumentConfig.Builder builder =
          ArgumentConfig.builder()
              .name(name)
              .type(type)
              .defaultValue(GraphQLValueUtils.parseValue(defaultValue));
      if (description != null) {
        builder.description(description);
      }
      arguments.add(builder.build());
      return this;
    }

    public Builder argument(String name, String type, String description, Value<?> defaultValue) {
      ArgumentConfig.Builder builder =
          ArgumentConfig.builder().name(name).type(type).defaultValue(defaultValue);
      if (description != null) {
        builder.description(description);
      }
      arguments.add(builder.build());
      return this;
    }

    public DirectiveConfig build() {
      if (locations == null) {
        locations = new ArrayList<>();
      }
      if (locations.isEmpty()) {
        locations.add(Introspection.DirectiveLocation.FIELD_DEFINITION);
      }

      if (definition == null) {
        // 构建参数字符串
        StringBuilder builder = new StringBuilder();
        if (description != null) {
          builder.append("\"").append(description).append("\" ");
        }
        builder.append("directive @").append(name);
        if (arguments != null && !arguments.isEmpty()) {
          builder.append("(");
          builder.append(
              arguments.stream()
                  .map(
                      arg -> {
                        StringBuilder argBuilder = new StringBuilder();
                        if (arg.getDescription() != null) {
                          argBuilder.append("\"").append(arg.getDescription()).append("\" ");
                        }
                        argBuilder.append(arg.getName());
                        argBuilder.append(": ").append(arg.getType());
                        if (arg.getDefaultValue() != null) {
                          argBuilder
                              .append(" = ")
                              .append(GraphQLValueUtils.convertToString(arg.getDefaultValue()));
                        }
                        return argBuilder.toString();
                      })
                  .collect(Collectors.joining(", ")));
          builder.append(")");
        }
        if (repeatable) {
          builder.append(" repeatable");
        }
        builder.append(" on ");
        builder.append(locations.stream().map(Enum::name).collect(Collectors.joining(" | ")));
        definition = builder.toString();
      }
      return new DirectiveConfig(
          name, description, repeatable, locations, arguments, definition, handler);
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @lombok.Builder(builderClassName = "Builder")
  public static class ArgumentConfig {
    private String name;
    private String type;
    private String description;
    private Value<?> defaultValue;
  }
}
