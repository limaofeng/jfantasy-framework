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
    GraphQLKickstartContext contextHolder = HOLDER.get();
    if (contextHolder != null) {
      HOLDER.remove();
    }
    HOLDER.set(context);
  }

  public static void clear() {
    GraphQLKickstartContext contextHolder = HOLDER.get();
    if (contextHolder != null) {
      HOLDER.remove();
    }
  }
}
