package org.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import org.jfantasy.graphql.gateway.config.GatewayConfig.ScalarConfig;

public interface ScalarTypeProvider {

  String getName();

  GraphQLScalarType getScalarType(ScalarConfig config);
}
