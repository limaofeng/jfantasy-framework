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
package net.asany.jfantasy.graphql.security.execution;

import graphql.ExecutionResult;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.NonNullableFieldWasNullException;
import java.util.concurrent.CompletableFuture;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mutation 执行策略
 *
 * @author limaofeng
 */
public class AsyncMutationExecutionStrategy extends AsyncExecutionStrategy {

  private final InterceptorManager interceptorManager;

  public AsyncMutationExecutionStrategy(InterceptorManager interceptorManager) {
    this.interceptorManager = interceptorManager;
  }

  @Override
  @Transactional
  public CompletableFuture<ExecutionResult> execute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters)
      throws NonNullableFieldWasNullException {

    // 使用 InterceptorManager 在执行前调用拦截器
    interceptorManager.executeBefore(executionContext, parameters);

    //    AuthGraphQLServletContext context = executionContext.getContext();
    //    GraphQLContextHolder.setContext(context);
    //    SecurityContextHolder.setContext(context.getSecurityContext());

    CompletableFuture<ExecutionResult> future = super.execute(executionContext, parameters);

    // 使用 InterceptorManager 在执行后调用拦截器
    return future.thenApply(
        result -> interceptorManager.executeAfter(executionContext, parameters, result));
  }
}
