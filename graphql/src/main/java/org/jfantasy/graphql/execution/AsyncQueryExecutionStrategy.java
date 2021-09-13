package org.jfantasy.graphql.execution;

import graphql.ExecutionResult;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.NonNullableFieldWasNullException;
import java.util.concurrent.CompletableFuture;
import org.jfantasy.framework.security.SecurityContextHolder;
import org.jfantasy.graphql.context.AuthorizationGraphQLServletContext;
import org.jfantasy.graphql.context.GraphQLContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 * 查询事务
 *
 * @author limaofeng
 */
public class AsyncQueryExecutionStrategy extends AsyncExecutionStrategy {

  @Override
  @Transactional(readOnly = true)
  public CompletableFuture<ExecutionResult> execute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters)
      throws NonNullableFieldWasNullException {
    AuthorizationGraphQLServletContext context = executionContext.getContext();
    GraphQLContextHolder.setContext(context);
    SecurityContextHolder.setContext(context.getSecurityContext());
    return super.execute(executionContext, parameters);
  }
}
