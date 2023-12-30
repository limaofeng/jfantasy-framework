package net.asany.jfantasy.graphql.gateway.data;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DelegatedFieldResolver implements DataFetcher<Object> {

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    GraphQLObjectType objectType = (GraphQLObjectType) environment.getParentType();

    GraphQLCodeRegistry codeRegistry = environment.getGraphQLSchema().getCodeRegistry();

    DataFetcher<?> dataFetcher =
        codeRegistry.getDataFetcher(objectType, environment.getFieldDefinition());

    return dataFetcher.get(environment);
  }
}
