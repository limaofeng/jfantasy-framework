package net.asany.jfantasy.graphql.gateway.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphQLServiceOverrideField {
  private String name;
  private String rename;
  @Builder.Default private boolean ignore = false;

  @Builder.Default
  private Map<String, GraphQLServiceOverrideFieldArgument> arguments = new HashMap<>();

  @Builder.Default
  private Map<String, GraphQLServiceOverrideFieldArgument> newArguments = new HashMap<>();

  public void renameArgument(String name, String newName) {
    if (!this.arguments.containsKey(name)) {
      this.arguments.put(name, GraphQLServiceOverrideFieldArgument.builder().name(name).build());
    }
    GraphQLServiceOverrideFieldArgument argument = this.arguments.get(name);
    argument.setName(name);
    argument.setRename(newName);
    this.newArguments.put(newName, argument);
  }

  public GraphQLServiceOverrideFieldArgument getArgument(String argumentName) {
    return this.arguments.get(argumentName);
  }
}
