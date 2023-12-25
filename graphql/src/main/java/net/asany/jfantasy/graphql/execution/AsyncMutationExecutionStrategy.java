package net.asany.jfantasy.graphql.execution;

import graphql.ExecutionResult;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.NonNullableFieldWasNullException;
import java.util.concurrent.CompletableFuture;
import net.asany.jfantasy.framework.security.SecurityContextHolder;
import net.asany.jfantasy.graphql.context.AuthorizationGraphQLServletContext;
import net.asany.jfantasy.graphql.context.GraphQLContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mutation 执行策略
 *
 * @author limaofeng
 */
public class AsyncMutationExecutionStrategy extends AsyncExecutionStrategy {

  @Override
  @Transactional
  public CompletableFuture<ExecutionResult> execute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters)
      throws NonNullableFieldWasNullException {
    AuthorizationGraphQLServletContext context = executionContext.getContext();
    GraphQLContextHolder.setContext(context);
    SecurityContextHolder.setContext(context.getSecurityContext());
    return super.execute(executionContext, parameters);
  }
}
