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
package net.asany.jfantasy.graphql.security.interceptors;

import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import net.asany.jfantasy.framework.security.SecurityContextHolder;
import net.asany.jfantasy.graphql.security.context.AuthGraphQLServletContext;
import net.asany.jfantasy.graphql.security.context.GraphQLContextHolder;
import net.asany.jfantasy.graphql.security.execution.ExecutionInterceptor;

public class AuthContextSetupInterceptor implements ExecutionInterceptor {

  @Override
  public void beforeExecute(
      ExecutionContext executionContext, ExecutionStrategyParameters parameters) {
    AuthGraphQLServletContext context = executionContext.getContext();
    GraphQLContextHolder.setContext(context);
    SecurityContextHolder.setContext(context.getSecurityContext());
  }
}
