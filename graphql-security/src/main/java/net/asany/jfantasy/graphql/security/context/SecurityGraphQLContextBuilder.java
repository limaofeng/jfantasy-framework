package net.asany.jfantasy.graphql.security.context;

import graphql.kickstart.execution.context.DefaultGraphQLContextBuilder;
import graphql.kickstart.execution.context.GraphQLKickstartContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.AuthenticationManager;
import net.asany.jfantasy.framework.security.SecurityContext;
import net.asany.jfantasy.framework.security.SecurityContextHolder;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.auth.apikey.ApiKeyAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.web.BearerTokenResolver;
import net.asany.jfantasy.framework.security.auth.oauth2.server.web.DefaultBearerTokenResolver;
import net.asany.jfantasy.framework.security.auth.oauth2.server.web.WebSocketBearerTokenResolver;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import net.asany.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
import net.asany.jfantasy.framework.security.authentication.SimpleAuthenticationToken;
import net.asany.jfantasy.framework.security.web.WebAuthenticationDetailsSource;
import net.asany.jfantasy.framework.security.web.WebSocketAuthenticationDetailsSource;
import org.dataloader.DataLoaderRegistry;
import org.springframework.core.log.LogMessage;

/**
 * 从请求头中获取 Token 并转化未用户
 *
 * @author limaofeng
 * @version V1.0
 */
@Slf4j
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
  public GraphQLKickstartContext build(HttpServletRequest req, HttpServletResponse response) {
    GraphQLContextHolder.clear();
    SecurityContextHolder.clear();
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(securityContext);

    AuthGraphQLServletContext context =
        new AuthGraphQLServletContext(this.dataLoaderRegistry, req, response, securityContext);
    GraphQLContextHolder.setContext(context);

    String token = bearerTokenResolver.resolve(req);

    AuthenticationDetails details = this.authenticationDetailsSource.buildDetails(req);

    return buildContext(
        context, token, details, () -> this.authenticationManagerResolver.resolve(req));
  }

  private AuthGraphQLServletContext buildContext(
      AuthGraphQLServletContext context,
      String token,
      AuthenticationDetails details,
      Supplier<AuthenticationManager> authenticationResolver) {
    if (token == null) {
      log.trace("Did not process request since did not find bearer token");
      SimpleAuthenticationToken<?> authenticationRequest = new SimpleAuthenticationToken<>();
      authenticationRequest.setDetails(details);
      context.setAuthentication(authenticationRequest);
      return context;
    }

    TokenType tokenType = TokenType.of(token);

    BearerTokenAuthenticationToken authenticationRequest =
        switch (tokenType) {
          case JWT -> new BearerTokenAuthenticationToken(token);
          case API_KEY -> new ApiKeyAuthenticationToken(token);
          case SESSION_ID -> throw new AuthenticationException("SESSION_ID 未实现");
          case PERSONAL_ACCESS_TOKEN -> throw new AuthenticationException(
              "PERSONAL_ACCESS_TOKEN 未实现");
        };
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
      log.warn(
          "An error occurred while attempting to authenticate the user with token {} error: {}",
          token,
          failed.getMessage());
      context.setAuthentication(authenticationRequest);
    }
    return context;
  }

  @Override
  public GraphQLKickstartContext build(Session session, HandshakeRequest request) {
    GraphQLContextHolder.clear();
    SecurityContextHolder.clear();

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(securityContext);

    String token = webSocketBearerTokenResolver.resolve(session);

    AuthGraphQLServletContext context =
        new AuthGraphQLServletContext(this.dataLoaderRegistry, session, request, securityContext);

    return buildContext(
        context,
        token,
        this.websocketAuthenticationDetailsSource.buildDetails(request),
        () -> this.websocketAuthenticationManagerResolver.resolve(request));
  }
}
