package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.GraphQLContext;

public class GraphQLContextHolder {

    private static ThreadLocal<AuthorizationGraphQLServletContext> holder = new ThreadLocal<>();

    public static AuthorizationGraphQLServletContext getContext() {
        GraphQLContext context = holder.get();
        if (context == null) {
            return null;
        }
        return holder.get();
    }

    public static void setContext(AuthorizationGraphQLServletContext context) {
        GraphQLContext contextHolder = holder.get();
        if (contextHolder != null) {
            holder.remove();
        }
        holder.set(context);
    }

    public static void clear() {
        GraphQLContext contextHolder = holder.get();
        if (contextHolder != null) {
            holder.remove();
        }
    }

}
