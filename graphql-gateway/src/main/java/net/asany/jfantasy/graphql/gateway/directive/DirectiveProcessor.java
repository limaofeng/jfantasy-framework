package net.asany.jfantasy.graphql.gateway.directive;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.execution.directives.QueryDirectives;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public class DirectiveProcessor {

  private final DirectiveFactory factory;

  public DirectiveProcessor(DirectiveFactory factory) {
    this.factory = factory;
  }

  // 根据环境生成新的 DataFetcher
  public DataFetcher<?> process(
      DataFetchingEnvironment environment, DataFetcher<?> originalDataFetcher) {
    DataFetcher<?> dataFetcher = originalDataFetcher;
    // 检查环境中的指令
    Field field = environment.getField();
    QueryDirectives queryDirectives = environment.getQueryDirectives();
    Map<Field, List<QueryAppliedDirective>> directivesByField =
        queryDirectives.getImmediateAppliedDirectivesByField();
    List<QueryAppliedDirective> directives = directivesByField.get(field);
    for (QueryAppliedDirective directive : directives) {
      dataFetcher = wrapWithDirective(environment, dataFetcher, directive);
    }
    return dataFetcher;
  }

  private DataFetcher<?> wrapWithDirective(
      DataFetchingEnvironment environment,
      DataFetcher<?> originalFetcher,
      QueryAppliedDirective directive) {
    GraphQLObjectType objectType = (GraphQLObjectType) environment.getParentType();
    GraphQLFieldDefinition fieldDefinition = environment.getFieldDefinition();
    ClientDirectiveHandler directiveHandler = factory.get(directive.getName());
    if (directiveHandler.isApplicable(objectType, fieldDefinition)) {
      return directiveHandler.apply(directive, originalFetcher);
    }
    return originalFetcher;
  }
}
