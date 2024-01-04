package net.asany.jfantasy.graphql.gateway.directive;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.language.DirectiveDefinition;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;

public class DateFormatDirective implements ClientDirectiveHandler {

  public static final DirectiveDefinition DEFINITION = GraphQLTypeUtils.parseDirectiveDefinition("""
    # 日期格式化
    directive @dateformat(
      # 日期格式
      format: String!
    ) on FIELD_DEFINITION | FIELD
    """);

  @Override
  public DataFetcher<?> apply(QueryAppliedDirective directive, DataFetcher<?> originalDataFetcher) {
    return (env) -> {
      Object result = originalDataFetcher.get(env);
      return result;
    };
  }

  @Override
  public boolean isApplicable(
      GraphQLObjectType objectType, GraphQLFieldDefinition fieldDefinition) {
    GraphQLNamedType namedType =
        (GraphQLNamedType) GraphQLTypeUtils.getSourceType(fieldDefinition.getType());
    return namedType.getName().equals("DateTime");
  }
}
