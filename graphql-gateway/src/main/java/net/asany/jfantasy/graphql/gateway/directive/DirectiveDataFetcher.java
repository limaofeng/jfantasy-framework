package net.asany.jfantasy.graphql.gateway.directive;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public interface DirectiveDataFetcher<T> extends DataFetcher<T> {

  @Override
  default T get(DataFetchingEnvironment environment) throws Exception {
    return null;
  }
}
