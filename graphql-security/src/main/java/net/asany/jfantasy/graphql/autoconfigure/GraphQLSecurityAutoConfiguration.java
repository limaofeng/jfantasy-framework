package net.asany.jfantasy.graphql.autoconfigure;

import graphql.execution.ExecutionStrategy;
import graphql.kickstart.autoconfigure.web.servlet.GraphQLWebAutoConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.HandshakeRequest;
import java.util.List;
import net.asany.jfantasy.autoconfigure.SecurityAutoConfiguration;
import net.asany.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
import net.asany.jfantasy.graphql.security.context.SecurityGraphQLContextBuilder;
import net.asany.jfantasy.graphql.security.execution.AsyncMutationExecutionStrategy;
import net.asany.jfantasy.graphql.security.execution.AsyncQueryExecutionStrategy;
import net.asany.jfantasy.graphql.security.execution.ExecutionInterceptor;
import net.asany.jfantasy.graphql.security.execution.InterceptorManager;
import net.asany.jfantasy.graphql.security.interceptors.AuthContextSetupInterceptor;
import org.dataloader.DataLoaderRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class GraphQLSecurityAutoConfiguration {

  @Bean
  @ConditionalOnClass(EnableWebMvc.class)
  public SecurityGraphQLContextBuilder securityGraphQLContextBuilder(
      AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
      AuthenticationManagerResolver<HandshakeRequest> websocketAuthenticationManagerResolver,
      DataLoaderRegistry dataLoaderRegistry) {
    return new SecurityGraphQLContextBuilder(
        authenticationManagerResolver, websocketAuthenticationManagerResolver, dataLoaderRegistry);
  }

  @Bean
  public InterceptorManager interceptorManager(List<ExecutionInterceptor> interceptors) {
    return new InterceptorManager(interceptors);
  }

  @Bean
  public ExecutionInterceptor authContextSetupInterceptor() {
    return new AuthContextSetupInterceptor();
  }

  @Bean(GraphQLWebAutoConfiguration.QUERY_EXECUTION_STRATEGY)
  @ConditionalOnMissingBean(name = GraphQLWebAutoConfiguration.QUERY_EXECUTION_STRATEGY)
  public ExecutionStrategy queryExecutionStrategy(InterceptorManager interceptorManager) {
    return new AsyncQueryExecutionStrategy(interceptorManager);
  }

  @Bean(GraphQLWebAutoConfiguration.MUTATION_EXECUTION_STRATEGY)
  @ConditionalOnMissingBean(name = GraphQLWebAutoConfiguration.MUTATION_EXECUTION_STRATEGY)
  public ExecutionStrategy mutationExecutionStrategy(InterceptorManager interceptorManager) {
    return new AsyncMutationExecutionStrategy(interceptorManager);
  }
}
