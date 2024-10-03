/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
