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
package net.asany.jfantasy.graphql.gateway;

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
