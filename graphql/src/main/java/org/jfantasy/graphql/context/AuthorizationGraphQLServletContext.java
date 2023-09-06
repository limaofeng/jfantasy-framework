package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.GraphQLKickstartContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import java.util.HashMap;
import java.util.Map;
import org.dataloader.DataLoaderRegistry;
import org.jetbrains.annotations.NotNull;
import org.jfantasy.framework.security.SecurityContext;
import org.jfantasy.framework.security.authentication.Authentication;

/**
 * 认证上下文对象
 *
 * @author limaofeng
 * @version V1.0
 */
public class AuthorizationGraphQLServletContext implements GraphQLKickstartContext {

  private HttpServletRequest request;
  private Session session;
  private HttpServletResponse response;
  private DataLoaderRegistry dataLoaderRegistry;
  private HandshakeRequest handshakeRequest;
  private Authentication authentication;
  private final SecurityContext securityContext;
  private final Map<String, Object> attributes = new HashMap<>();

  public AuthorizationGraphQLServletContext(
      HttpServletRequest request, HttpServletResponse response, SecurityContext securityContext) {
    this.request = request;
    this.response = response;
    this.securityContext = securityContext;
  }

  public AuthorizationGraphQLServletContext(
      Session session, HandshakeRequest request, SecurityContext securityContext) {
    this.session = session;
    this.handshakeRequest = request;
    this.securityContext = securityContext;
  }

  //  @Override
  //  public Optional<Subject> getSubject() {
  //    return Optional.empty();
  //  }

  @NotNull
  @Override
  public DataLoaderRegistry getDataLoaderRegistry() {
    return dataLoaderRegistry;
  }

  @Override
  public Map<Object, Object> getMapOfContext() {
    return null;
  }

  public void setDataLoaderRegistry(DataLoaderRegistry dataLoaderRegistry) {
    this.dataLoaderRegistry = dataLoaderRegistry;
  }

  public HandshakeRequest getHandshakeRequest() {
    return handshakeRequest;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public Session getSession() {
    return session;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public Authentication getAuthentication() {
    return authentication;
  }

  public <T> void setAttribute(String name, T o) {
    attributes.put(name, o);
  }

  public <T> T getAttribute(String name) {
    return (T) attributes.get(name);
  }

  public void setAuthentication(Authentication authentication) {
    this.authentication = authentication;
    securityContext.setAuthentication(authentication);
  }

  public SecurityContext getSecurityContext() {
    return securityContext;
  }
}
