package cn.asany.example.demo;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import java.util.Objects;
import net.asany.jfantasy.graphql.gateway.directive.ClientDirectiveHandler;

public class MyDirective implements ClientDirectiveHandler {
  @Override
  public DataFetcher<?> apply(QueryAppliedDirective directive, DataFetcher<?> originalDataFetcher) {
    return (env) -> {
      Object result = originalDataFetcher.get(env);
      String arg1Value = Objects.requireNonNull(directive.getArgument("arg1")).getValue();
      return arg1Value + result;
    };
  }

  @Override
  public boolean isApplicable(
      GraphQLObjectType objectType, GraphQLFieldDefinition fieldDefinition) {
    return true;
  }
}
