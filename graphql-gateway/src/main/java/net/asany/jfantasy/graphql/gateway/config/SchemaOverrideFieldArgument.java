package net.asany.jfantasy.graphql.gateway.config;

import graphql.language.Value;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;

@Builder
@Data
public class SchemaOverrideFieldArgument {
  private String name;
  private String mapping;
  private boolean exclude;
  private String value;
  private String defaultValue;
  private boolean extended;

  public boolean isFixedValue() {
    return exclude && this.value != null;
  }

  public boolean isNameChanged() {
    return !this.mapping.equals(this.name);
  }

  public Value<?> getGraphQLValue() {
    return GraphQLValueUtils.parseValue(this.value);
  }
}
