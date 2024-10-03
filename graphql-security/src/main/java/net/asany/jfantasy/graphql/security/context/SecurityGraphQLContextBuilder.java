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
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.Token;
import net.asany.jfantasy.framework.security.auth.apikey.ApiKeyAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.base.AnonymousAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.base.AnonymousTokenResolver;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.web.*;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import net.asany.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
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

  private final CompositeTokenResolver<HttpServletRequest> tokenResolver =
      new CompositeTokenResolver<>(new AnonymousTokenResolver(), new DefaultBearerTokenResolver());
  private final CompositeTokenResolver<Session> webSocketBearerTokenResolver =
      new CompositeTokenResolver<>(new WebSocketBearerTokenResolver());

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

    Token token = tokenResolver.resolveToken(req);

    AuthenticationDetails details = this.authenticationDetailsSource.buildDetails(req);

    return buildContext(
        context, token, details, () -> this.authenticationManagerResolver.resolve(req));
  }

  private AuthGraphQLServletContext buildContext(
      AuthGraphQLServletContext context,
      Token token,
      AuthenticationDetails details,
      Supplier<AuthenticationManager> authenticationResolver) {
    if (token == null) {
      log.trace("Did not process request since did not find bearer token");
      AnonymousAuthenticationToken authenticationRequest =
          new AnonymousAuthenticationToken("anonymous");
      authenticationRequest.setDetails(details);
      context.setAuthentication(authenticationRequest);
      return context;
    }

    AuthenticationToken<String> authenticationRequest =
        switch (token.tokenType()) {
          case ACCESS_TOKEN -> new BearerTokenAuthenticationToken(token.value(), details);
          case API_KEY -> new ApiKeyAuthenticationToken(token.value(), details);
          case ANONYMOUS -> new AnonymousAuthenticationToken(token.value(), details);
          default -> throw new AuthenticationException("Unsupported token type");
        };

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

    Token token = webSocketBearerTokenResolver.resolveToken(session);

    AuthGraphQLServletContext context =
        new AuthGraphQLServletContext(this.dataLoaderRegistry, session, request, securityContext);

    return buildContext(
        context,
        token,
        this.websocketAuthenticationDetailsSource.buildDetails(request),
        () -> this.websocketAuthenticationManagerResolver.resolve(request));
  }
}
