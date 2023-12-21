package org.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.graphql.gateway.config.GatewayConfig;

@Slf4j
public class ExtendedScalarsTypeProvider implements ScalarTypeProvider {

  @Override
  public String getName() {
    return "extended-scalars";
  }

  @Override
  public GraphQLScalarType getScalarType(GatewayConfig.ScalarConfig config) {
    String name = Optional.ofNullable(config.getResolver()).orElse(config.getName());
    try {
      return (GraphQLScalarType)
          Class.forName("graphql.scalars.ExtendedScalars").getField(name).get(null);
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
