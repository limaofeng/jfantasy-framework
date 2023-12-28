package net.asany.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;

public class ScalarTypeResolver {

  private final Map<String, GatewayConfig.ScalarConfig> scalarConfigMap = new HashMap<>();

  private final ScalarTypeProviderFactory factory;

  public ScalarTypeResolver(ScalarTypeProviderFactory factory) {
    this.factory = factory;
  }

  public GraphQLScalarType resolveScalarType(String name) {
    if (this.scalarConfigMap.containsKey(name)) {
      GatewayConfig.ScalarConfig scalarConfig = this.scalarConfigMap.get(name);
      String provider = scalarConfig.getProvider();
      if (provider == null) {
        return factory.getDefaultScalarTypeProvider().getScalarType(scalarConfig);
      }
      return factory.getProvider(scalarConfig.getProvider()).getScalarType(scalarConfig);
    }
    GraphQLScalarType scalarType =
        factory
            .getProvider("spring")
            .getScalarType(GatewayConfig.ScalarConfig.builder().name(name).build());
    if (scalarType != null) {
      return scalarType;
    }
    scalarType =
        factory
            .getProvider("extended-scalars")
            .getScalarType(GatewayConfig.ScalarConfig.builder().name(name).build());
    if (scalarType != null) {
      return scalarType;
    }
    return factory
        .getDefaultScalarTypeProvider()
        .getScalarType(GatewayConfig.ScalarConfig.builder().name(name).build());
  }

  public void setScalars(List<GatewayConfig.ScalarConfig> scalars) {
    this.scalarConfigMap.putAll(
        scalars.stream()
            .collect(Collectors.toMap(GatewayConfig.ScalarConfig::getName, Function.identity())));
  }
}
