package net.asany.jfantasy.graphql.gateway.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SchemaOverrideFieldArgument {
  private String name;
  private String mapping;
  private boolean exclude;
  private Boolean defaultValue;

  public boolean isNameChanged() {
    return !this.mapping.equals(this.name);
  }
}
