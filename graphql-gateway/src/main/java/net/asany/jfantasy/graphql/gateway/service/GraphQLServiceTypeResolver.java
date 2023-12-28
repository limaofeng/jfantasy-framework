package net.asany.jfantasy.graphql.gateway.service;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class GraphQLServiceTypeResolver implements TypeResolver {

  public GraphQLServiceTypeResolver(GraphQLService service) {}

  @Override
  public GraphQLObjectType getType(TypeResolutionEnvironment env) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
