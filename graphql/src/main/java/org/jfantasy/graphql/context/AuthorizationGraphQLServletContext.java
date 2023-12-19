package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.DefaultGraphQLContext;
import graphql.kickstart.execution.context.GraphQLKickstartContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import java.util.HashMap;
import java.util.Map;
import org.dataloader.DataLoaderRegistry;
import org.jfantasy.framework.security.SecurityContext;
import org.jfantasy.framework.security.authentication.Authentication;

/**
 * 认证上下文对象
 *
 * @author limaofeng
 * @version V1.0
 */
public class AuthorizationGraphQLServletContext extends DefaultGraphQLContext
    implements GraphQLKickstartContext {

  private static final String DATA_HTTP_REQUEST = "http.request";
  private static final String DATA_WEBSOCKET_SESSION = "websocket.session";
  private static final String DATA_HTTP_RESPONSE = "http.response";
  private static final String DATA_WEBSOCKET_HANDSHAKE_REQUEST = "websocket.handshake.request";
  private static final String DATA_AUTHENTICATION = "authentication";
  private static final String DATA_SECURITY_CONTEXT = "security.context";

  public AuthorizationGraphQLServletContext(
      DataLoaderRegistry dataLoaderRegistry,
      HttpServletRequest request,
      HttpServletResponse response,
      SecurityContext securityContext) {
    super(dataLoaderRegistry, createGraphQLContextMap(request, response, securityContext));
  }

  public AuthorizationGraphQLServletContext(
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

  @SuppressWarnings("NullableProblems")
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
    //noinspection unchecked
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
