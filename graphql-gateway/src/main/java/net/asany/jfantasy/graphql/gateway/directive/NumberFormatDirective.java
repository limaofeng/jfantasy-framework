package net.asany.jfantasy.graphql.gateway.directive;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.language.DirectiveDefinition;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;

public class NumberFormatDirective implements ClientDirectiveHandler {

  public static final DirectiveDefinition DEFINITION = GraphQLTypeUtils.parseDirectiveDefinition("""
    # 数字格式化
    directive @numberformat(
      # 小数位数
      decimals: Int = 2,
      # 千位分隔符
      useGrouping: Boolean = true
    ) on FIELD_DEFINITION | FIELD
    """);

  @Override
  public DataFetcher<?> apply(QueryAppliedDirective directive, DataFetcher<?> originalFetcher) {
    return (env) -> {
      Object rawNumber = originalFetcher.get(env);
      if (!(rawNumber instanceof Number)) {
        return rawNumber;
      }
      //      Number number = (Number) rawNumber;
      //      String formatString = (String)
      // environment.getDirective().getArgument("format").getArgumentValue().getValue();
      return rawNumber;
    };
  }

  @Override
  public boolean isApplicable(
      GraphQLObjectType objectType, GraphQLFieldDefinition fieldDefinition) {
    return false;
  }
}
