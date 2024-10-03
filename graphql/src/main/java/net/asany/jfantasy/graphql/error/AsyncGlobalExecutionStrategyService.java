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
package net.asany.jfantasy.graphql.error;

import graphql.ErrorClassification;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLError;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.NonNullableFieldWasNullException;
import graphql.language.SourceLocation;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncGlobalExecutionStrategyService extends AsyncExecutionStrategy {

  @Override
  public CompletableFuture<ExecutionResult> execute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters)
      throws NonNullableFieldWasNullException {
    CompletableFuture<ExecutionResult> overallResult = super.execute(executionContext, parameters);

    return overallResult.handleAsync(
        (ExecutionResult results, Throwable exception) -> {
          if (results.getErrors().isEmpty()) {
            return results;
          }
          return new ExecutionResultImpl(
              new GraphQLError() {
                @Override
                public String getMessage() {
                  return "1111";
                }

                @Override
                public List<SourceLocation> getLocations() {
                  return null;
                }

                @Override
                public ErrorClassification getErrorType() {
                  return null;
                }
              });
        });
  }
}
