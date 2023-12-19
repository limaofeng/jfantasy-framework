package org.jfantasy.graphql.gateway.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GraphQLServiceOverrideFieldArgument {
  private String name;
  private String rename;
}
