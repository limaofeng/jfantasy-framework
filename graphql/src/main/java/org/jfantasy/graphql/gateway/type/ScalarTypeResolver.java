package org.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jfantasy.graphql.gateway.config.GatewayConfig.ScalarConfig;

public class ScalarTypeResolver {

  private final Map<String, ScalarConfig> scalarConfigMap = new HashMap<>();

  private final ScalarTypeProviderFactory factory;

  public ScalarTypeResolver(ScalarTypeProviderFactory factory) {
    this.factory = factory;
  }

  public GraphQLScalarType resolveScalarType(String name) {
    if (this.scalarConfigMap.containsKey(name)) {
      ScalarConfig scalarConfig = this.scalarConfigMap.get(name);
      String provider = scalarConfig.getProvider();
      if (provider == null) {
        return factory.getDefaultScalarTypeProvider().getScalarType(scalarConfig);
      }
      return factory.getProvider(scalarConfig.getProvider()).getScalarType(scalarConfig);
    }
    GraphQLScalarType scalarType =
        factory.getProvider("spring").getScalarType(ScalarConfig.builder().name(name).build());
    if (scalarType != null) {
      return scalarType;
    }
    scalarType =
        factory
            .getProvider("extended-scalars")
            .getScalarType(ScalarConfig.builder().name(name).build());
    if (scalarType != null) {
      return scalarType;
    }
    return factory
        .getDefaultScalarTypeProvider()
        .getScalarType(ScalarConfig.builder().name(name).build());
  }

  public void setScalars(List<ScalarConfig> scalars) {
    this.scalarConfigMap.putAll(
        scalars.stream().collect(Collectors.toMap(ScalarConfig::getName, Function.identity())));
  }
}
