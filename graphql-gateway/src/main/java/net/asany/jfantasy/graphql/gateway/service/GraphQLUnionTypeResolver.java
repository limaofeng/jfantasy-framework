package net.asany.jfantasy.graphql.gateway.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class GraphQLUnionTypeResolver implements TypeResolver {
  @Override
  public GraphQLObjectType getType(TypeResolutionEnvironment env) {
    ObjectNode objectNode = env.getObject();
    String typeName = objectNode.get("__typename").asText();
    return env.getSchema().getObjectType(typeName);
  }
}
