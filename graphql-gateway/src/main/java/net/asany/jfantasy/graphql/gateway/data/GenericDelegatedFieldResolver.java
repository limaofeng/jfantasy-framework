package net.asany.jfantasy.graphql.gateway.data;

import graphql.language.*;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.util.GraphQLUtils;

@Slf4j
public class GenericDelegatedFieldResolver implements DelegatedFieldResolver<Object> {

  private final String resolve;

  public GenericDelegatedFieldResolver(String resolve) {
    this.resolve = resolve;
  }

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    Field field = environment.getField();
    GraphQLObjectType objectType = (GraphQLObjectType) environment.getParentType();

    String gql = GraphQLUtils.buildGraphQLQuery(environment);

    log.warn("gql:" + gql);

    GraphQLCodeRegistry codeRegistry = environment.getGraphQLSchema().getCodeRegistry();

    DataFetcher<?> dataFetcher =
        codeRegistry.getDataFetcher(objectType, environment.getFieldDefinition());

    return dataFetcher.get(environment);
  }
}
