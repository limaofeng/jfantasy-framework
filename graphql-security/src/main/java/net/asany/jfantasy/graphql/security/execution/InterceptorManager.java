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
