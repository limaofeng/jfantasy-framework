package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.GraphQLContext;

/**
 * GraphQL 上下文对象
 *
 * @author limaofeng
 */
public class GraphQLContextHolder {

  private static final ThreadLocal<AuthorizationGraphQLServletContext> HOLDER = new ThreadLocal<>();

  public static AuthorizationGraphQLServletContext getContext() {
    GraphQLContext context = HOLDER.get();
    if (context == null) {
      return null;
    }
    return HOLDER.get();
  }

  public static void setContext(AuthorizationGraphQLServletContext context) {
    GraphQLContext contextHolder = HOLDER.get();
    if (contextHolder != null) {
      HOLDER.remove();
    }
    HOLDER.set(context);
  }

  public static void clear() {
    GraphQLContext contextHolder = HOLDER.get();
    if (contextHolder != null) {
      HOLDER.remove();
    }
  }
}
