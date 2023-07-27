package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.DefaultGraphQLContextBuilder;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoaderRegistry;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.AuthenticationManager;
import org.jfantasy.framework.security.SecurityContext;
import org.jfantasy.framework.security.SecurityContextHolder;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import org.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
import org.jfantasy.framework.security.authentication.SimpleAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.web.BearerTokenResolver;
import org.jfantasy.framework.security.oauth2.server.web.DefaultBearerTokenResolver;
import org.jfantasy.framework.security.oauth2.server.web.WebSocketBearerTokenResolver;
import org.jfantasy.framework.security.web.WebAuthenticationDetailsSource;
import org.jfantasy.framework.security.web.WebSocketAuthenticationDetailsSource;
import org.springframework.core.log.LogMessage;
import org.springframework.stereotype.Component;

/**
 * 从请求头中获取 Token 并转化未用户
 *
 * @author limaofeng
 * @version V1.0
 */
@Slf4j
@Component
public class SecurityGraphQLContextBuilder extends DefaultGraphQLContextBuilder
    implements GraphQLServletContextBuilder {

  private final BearerTokenResolver<HttpServletRequest> bearerTokenResolver =
      new DefaultBearerTokenResolver();
  private final BearerTokenResolver<Session> webSocketBearerTokenResolver =
      new WebSocketBearerTokenResolver();

  private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource =
      new WebAuthenticationDetailsSource();

  private final AuthenticationDetailsSource<HandshakeRequest, ?>
      websocketAuthenticationDetailsSource = new WebSocketAuthenticationDetailsSource();

  private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;
  private final AuthenticationManagerResolver<HandshakeRequest>
      websocketAuthenticationManagerResolver;

  private final DataLoaderRegistry dataLoaderRegistry;

  public SecurityGraphQLContextBuilder(
      AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
      AuthenticationManagerResolver<HandshakeRequest> websocketAuthenticationManagerResolver,
      DataLoaderRegistry dataLoaderRegistry) {
    this.authenticationManagerResolver = authenticationManagerResolver;
    this.websocketAuthenticationManagerResolver = websocketAuthenticationManagerResolver;
    this.dataLoaderRegistry = dataLoaderRegistry;
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
    GraphQLContextHolder.clear();
    SecurityContextHolder.clear();

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(securityContext);

    AuthorizationGraphQLServletContext context =
        new AuthorizationGraphQLServletContext(req, response, securityContext);
    context.setDataLoaderRegistry(this.dataLoaderRegistry);
    GraphQLContextHolder.setContext(context);

    String token = bearerTokenResolver.resolve(req);

    Object details = this.authenticationDetailsSource.buildDetails(req);

    return buildContext(
        context, token, details, () -> this.authenticationManagerResolver.resolve(req));
  }

  private AuthorizationGraphQLServletContext buildContext(
      AuthorizationGraphQLServletContext context,
      String token,
      Object details,
      Supplier<AuthenticationManager> authenticationResolver) {
    if (token == null) {
      log.trace("Did not process request since did not find bearer token");
      SimpleAuthenticationToken<?> authenticationRequest = new SimpleAuthenticationToken<>();
      authenticationRequest.setDetails(details);
      context.setAuthentication(authenticationRequest);
      return context;
    }

    BearerTokenAuthenticationToken authenticationRequest =
        new BearerTokenAuthenticationToken(token);
    authenticationRequest.setDetails(details);

    try {
      AuthenticationManager authenticationManager = authenticationResolver.get();
      Authentication authenticationResult =
          authenticationManager.authenticate(authenticationRequest);

      if (log.isDebugEnabled()) {
        log.debug(
            LogMessage.format("Set SecurityContextHolder to %s", authenticationResult).toString());
      }

      context.setAuthentication(authenticationResult);
    } catch (AuthenticationException failed) {
      log.info("Failed to process authentication request", failed);
    }
    return context;
  }

  @Override
  public GraphQLContext build(Session session, HandshakeRequest request) {
    GraphQLContextHolder.clear();
    SecurityContextHolder.clear();

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(securityContext);

    String token = webSocketBearerTokenResolver.resolve(session);

    AuthorizationGraphQLServletContext context =
        new AuthorizationGraphQLServletContext(session, request, securityContext);

    context.setDataLoaderRegistry(this.dataLoaderRegistry);
    return buildContext(
        context,
        token,
        this.websocketAuthenticationDetailsSource.buildDetails(request),
        () -> this.websocketAuthenticationManagerResolver.resolve(request));
  }
}
