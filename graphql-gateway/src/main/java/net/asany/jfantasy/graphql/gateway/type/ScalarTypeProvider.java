package net.asany.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;

public interface ScalarTypeProvider {

  String getName();

  GraphQLScalarType getScalarType(GatewayConfig.ScalarConfig config);
}
