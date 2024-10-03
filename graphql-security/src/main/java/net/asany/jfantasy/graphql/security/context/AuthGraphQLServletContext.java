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

import graphql.kickstart.execution.context.DefaultGraphQLContext;
import graphql.kickstart.execution.context.GraphQLKickstartContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.SecurityContext;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import org.dataloader.DataLoaderRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * 认证上下文对象
 *
 * @author limaofeng
 * @version V1.0
 */
public class AuthGraphQLServletContext extends DefaultGraphQLContext
    implements GraphQLKickstartContext {

  private static final String DATA_HTTP_REQUEST = "http.request";
  private static final String DATA_WEBSOCKET_SESSION = "websocket.session";
  private static final String DATA_HTTP_RESPONSE = "http.response";
  private static final String DATA_WEBSOCKET_HANDSHAKE_REQUEST = "websocket.handshake.request";
  private static final String DATA_AUTHENTICATION = "authentication";
  private static final String DATA_SECURITY_CONTEXT = "security.context";

  public AuthGraphQLServletContext(
      DataLoaderRegistry dataLoaderRegistry,
      HttpServletRequest request,
      HttpServletResponse response,
      SecurityContext securityContext) {
    super(dataLoaderRegistry, createGraphQLContextMap(request, response, securityContext));
  }

  public AuthGraphQLServletContext(
      DataLoaderRegistry dataLoaderRegistry,
      Session session,
      HandshakeRequest request,
      SecurityContext securityContext) {
    super(dataLoaderRegistry, createGraphQLContextMap(session, request, securityContext));
  }

  private static Map<Object, Object> createGraphQLContextMap(
      HttpServletRequest request, HttpServletResponse response, SecurityContext securityContext) {
    Map<Object, Object> map = new HashMap<>();
    map.put(DATA_HTTP_REQUEST, request);
    map.put(DATA_HTTP_RESPONSE, response);
    map.put(DATA_SECURITY_CONTEXT, securityContext);
    return map;
  }

  private static Map<Object, Object> createGraphQLContextMap(
      Session session, HandshakeRequest request, SecurityContext securityContext) {
    Map<Object, Object> map = new HashMap<>();
    map.put(DATA_WEBSOCKET_SESSION, session);
    map.put(DATA_WEBSOCKET_HANDSHAKE_REQUEST, request);
    map.put(DATA_SECURITY_CONTEXT, securityContext);
    return map;
  }

  @NotNull
  @Override
  public DataLoaderRegistry getDataLoaderRegistry() {
    return super.getDataLoaderRegistry();
  }

  public <T> T getValue(String key, Class<T> valueType) {
    return valueType.cast(super.getMapOfContext().get(key));
  }

  public HandshakeRequest getHandshakeRequest() {
    return getValue(DATA_WEBSOCKET_HANDSHAKE_REQUEST, HandshakeRequest.class);
  }

  public HttpServletRequest getRequest() {
    return getValue(DATA_HTTP_REQUEST, HttpServletRequest.class);
  }

  public Session getSession() {
    return getValue(DATA_WEBSOCKET_SESSION, Session.class);
  }

  public HttpServletResponse getResponse() {
    return getValue(DATA_HTTP_RESPONSE, HttpServletResponse.class);
  }

  public Authentication getAuthentication() {
    return getValue(DATA_AUTHENTICATION, Authentication.class);
  }

  public <T> void setAttribute(String name, T o) {
    super.put(name, o);
  }

  public <T> T getAttribute(String name) {
    return (T) getMapOfContext().get(name);
  }

  public void setAuthentication(Authentication authentication) {
    super.put(DATA_AUTHENTICATION, authentication);
    getSecurityContext().setAuthentication(authentication);
  }

  public SecurityContext getSecurityContext() {
    return getValue(DATA_SECURITY_CONTEXT, SecurityContext.class);
  }
}
