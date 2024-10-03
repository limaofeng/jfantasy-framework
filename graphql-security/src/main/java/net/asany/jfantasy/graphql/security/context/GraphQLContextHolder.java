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
package net.asany.jfantasy.graphql.security.context;

import graphql.kickstart.execution.context.GraphQLKickstartContext;

/**
 * GraphQL 上下文对象
 *
 * @author limaofeng
 */
public class GraphQLContextHolder {

  private static final ThreadLocal<AuthGraphQLServletContext> HOLDER = new ThreadLocal<>();

  public static AuthGraphQLServletContext getContext() {
    GraphQLKickstartContext context = HOLDER.get();
    if (context == null) {
      return null;
    }
    return HOLDER.get();
  }

  public static void setContext(AuthGraphQLServletContext context) {
    clear();
    HOLDER.set(context);
  }

  public static void clear() {
    GraphQLKickstartContext contextHolder = HOLDER.get();
    if (contextHolder != null) {
      HOLDER.remove();
    }
  }
}
