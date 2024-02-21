package net.asany.jfantasy.graphql.security.execution;

import graphql.ExecutionResult;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import java.util.List;

public class InterceptorManager {

  private final List<ExecutionInterceptor> interceptors;

  public InterceptorManager(List<ExecutionInterceptor> interceptors) {
    this.interceptors = interceptors;
  }

  public void executeBefore(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters) {
    interceptors.stream()
        .filter(interceptor -> interceptor.shouldApply(executionContext, parameters))
        .forEach(interceptor -> interceptor.beforeExecute(executionContext, parameters));
  }

  public ExecutionResult executeAfter(
      ExecutionContext executionContext,
      ExecutionStrategyParameters parameters,
      ExecutionResult result) {
    ExecutionResult modifiedResult = result;
    for (ExecutionInterceptor interceptor : interceptors) {
      if (interceptor.shouldApply(executionContext, parameters)) {
        modifiedResult = interceptor.afterExecute(executionContext, parameters, modifiedResult);
      }
    }
    return modifiedResult;
  }
}
