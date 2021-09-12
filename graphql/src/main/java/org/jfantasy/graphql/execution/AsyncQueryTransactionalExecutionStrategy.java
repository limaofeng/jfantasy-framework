package org.jfantasy.graphql.execution;

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
public class AsyncQueryTransactionalExecutionStrategy extends AsyncExecutionStrategy {

  @Override
  @Transactional(readOnly = true)
  public CompletableFuture<ExecutionResult> execute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters)
      throws NonNullableFieldWasNullException {
    return super.execute(executionContext, parameters);
  }
}
