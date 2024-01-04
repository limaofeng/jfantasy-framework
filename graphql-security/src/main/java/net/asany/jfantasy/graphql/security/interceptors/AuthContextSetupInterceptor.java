package net.asany.jfantasy.graphql.security.interceptors;

import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategyParameters;
import net.asany.jfantasy.framework.security.SecurityContextHolder;
import net.asany.jfantasy.graphql.security.context.AuthGraphQLServletContext;
import net.asany.jfantasy.graphql.security.context.GraphQLContextHolder;
import net.asany.jfantasy.graphql.security.execution.ExecutionInterceptor;

public class AuthContextSetupInterceptor implements ExecutionInterceptor {

  @Override
  public void beforeExecute(ExecutionContext executionContext, ExecutionStrategyParameters parameters) {
    AuthGraphQLServletContext context = executionContext.getContext();
    GraphQLContextHolder.setContext(context);
    SecurityContextHolder.setContext(context.getSecurityContext());
  }
}
