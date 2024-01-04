package net.asany.jfantasy.graphql.gateway.directive;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

public interface ClientDirectiveHandler {

  /**
   * 应用指令到特定的字段。
   *
   * @param directive 指令
   * @param originalDataFetcher 原始的 DataFetcher。
   * @return 修改后的 DataFetcher。
   */
  DataFetcher<?> apply(QueryAppliedDirective directive, DataFetcher<?> originalDataFetcher);

  /**
   * 检查是否应该应用这个指令。
   *
   * @param objectType GraphQL 对象类型。
   * @param fieldDefinition 字段定义。
   * @return 如果指令适用于此字段，则为 true。
   */
  boolean isApplicable(GraphQLObjectType objectType, GraphQLFieldDefinition fieldDefinition);
}
