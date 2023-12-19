package org.jfantasy.graphql.gateway.service;

import graphql.language.TypeDefinition;
import graphql.schema.*;
import java.io.IOException;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverride;

public interface GraphQLService {

  String getName();

  GraphQLServiceOverride getOverrideConfig();

  GraphQLSchema makeSchema() throws IOException;

  GraphQLInputType getInputType(String name);

  TypeDefinition<?> getTypeDefinition(String name);

  GraphQLOutputType getOutputType(String name);

  boolean hasType(String name);

  void addType(String name, GraphQLType build);
}
