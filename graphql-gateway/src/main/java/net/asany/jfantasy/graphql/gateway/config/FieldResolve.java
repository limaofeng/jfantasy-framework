package net.asany.jfantasy.graphql.gateway.config;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder")
public class FieldResolve {
  private String query;
  private Map<String, String> arguments;
}
