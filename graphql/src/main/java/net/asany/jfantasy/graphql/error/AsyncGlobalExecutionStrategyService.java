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
