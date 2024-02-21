package net.asany.jfantasy.graphql.security.execution;

import graphql.ExecutionResult;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;

public interface ExecutionInterceptor {

  default boolean shouldApply(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters) {
    return true;
  }

  default void beforeExecute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters) {}

  default ExecutionResult afterExecute(
      ExecutionContext executionContext,
      ExecutionStrategyParameters parameters,
      ExecutionResult result) {
    return result;
  }
}
