package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.DefaultGraphQLContextBuilder;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.AuthenticationManager;
import org.jfantasy.framework.security.SecurityContext;
import org.jfantasy.framework.security.SecurityContextHolder;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import org.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.web.BearerTokenResolver;
import org.jfantasy.framework.security.oauth2.server.web.DefaultBearerTokenResolver;
import org.jfantasy.framework.security.web.WebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.stereotype.Component;

/**
 * 从请求头中获取 Token 并转化未用户
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-14 14:13
 */
@Slf4j
@Component
public class SecurityGraphQLContextBuilder extends DefaultGraphQLContextBuilder
    implements GraphQLServletContextBuilder {

  private final BearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();

  private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource =
      new WebAuthenticationDetailsSource();

  @Autowired
  private AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

  @Override
  @Transactional
  public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
    SecurityContextHolder.clearContext();

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(securityContext);

    AuthorizationGraphQLServletContext context =
        new AuthorizationGraphQLServletContext(req, response, securityContext);
    context.setDataLoaderRegistry(buildDataLoaderRegistry());
    GraphQLContextHolder.setContext(context);

    String token = bearerTokenResolver.resolve(req);

    if (token == null) {
      log.trace("Did not process request since did not find bearer token");
      return context;
    }

    BearerTokenAuthenticationToken authenticationRequest =
        new BearerTokenAuthenticationToken(token);
    authenticationRequest.setDetails(this.authenticationDetailsSource.buildDetails(req));

    try {
      AuthenticationManager authenticationManager = this.authenticationManagerResolver.resolve(req);
      Authentication authenticationResult =
          authenticationManager.authenticate(authenticationRequest);

      if (log.isDebugEnabled()) {
        log.debug(
            LogMessage.format("Set SecurityContextHolder to %s", authenticationResult).toString());
      }

      context.setAuthentication(authenticationResult);
    } catch (AuthenticationException failed) {
      SecurityContextHolder.clearContext();
      log.trace("Failed to process authentication request", failed);
      throw failed;
    }
    return context;
  }

  @Override
  public GraphQLContext build(Session session, HandshakeRequest request) {
    AuthorizationGraphQLServletContext context =
        new AuthorizationGraphQLServletContext(session, request);
    context.setDataLoaderRegistry(buildDataLoaderRegistry());
    return context;
  }

  private DataLoaderRegistry buildDataLoaderRegistry() {
    DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
    dataLoaderRegistry.register(
        "customerDataLoader",
        new DataLoader<Integer, String>(
            customerIds ->
                CompletableFuture.supplyAsync(
                    () -> {
                      System.out.println(customerIds);
                      return new ArrayList<>();
                    })));
    return dataLoaderRegistry;
  }
}
