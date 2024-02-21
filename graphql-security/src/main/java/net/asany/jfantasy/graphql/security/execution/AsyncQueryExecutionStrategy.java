package net.asany.jfantasy.graphql.security.execution;

import graphql.ExecutionResult;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.NonNullableFieldWasNullException;
import java.util.concurrent.CompletableFuture;
import org.springframework.transaction.annotation.Transactional;

/**
 * 查询事务
 *
 * @author limaofeng
 */
public class AsyncQueryExecutionStrategy extends AsyncExecutionStrategy {

  private InterceptorManager interceptorManager;

  public AsyncQueryExecutionStrategy(InterceptorManager interceptorManager) {
    this.interceptorManager = interceptorManager;
  }

  @Override
  @Transactional(readOnly = true)
  public CompletableFuture<ExecutionResult> execute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters)
      throws NonNullableFieldWasNullException {

    // 使用 InterceptorManager 在执行前调用拦截器
    interceptorManager.executeBefore(executionContext, parameters);

    CompletableFuture<ExecutionResult> future = super.execute(executionContext, parameters);

    // 使用 InterceptorManager 在执行后调用拦截器
    return future.thenApply(
        result -> interceptorManager.executeAfter(executionContext, parameters, result));
  }
}
