package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.GraphQLContext;
import java.util.Optional;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import org.dataloader.DataLoaderRegistry;
import org.jfantasy.framework.security.SecurityContext;
import org.jfantasy.framework.security.authentication.Authentication;

/**
 * 认证上下文对象
 *
 * @author limaofeng
 * @version V1.0
 * @date 2020/4/26 11:33 上午
 */
public class AuthorizationGraphQLServletContext implements GraphQLContext {

  private HttpServletRequest request;
  private Session session;
  private HttpServletResponse response;
  private DataLoaderRegistry dataLoaderRegistry;
  private HandshakeRequest handshakeRequest;
  private Authentication authentication;
  private SecurityContext securityContext;

  public AuthorizationGraphQLServletContext(
      HttpServletRequest request, HttpServletResponse response, SecurityContext securityContext) {
    this.request = request;
    this.response = response;
    this.securityContext = securityContext;
  }

  public AuthorizationGraphQLServletContext(Session session, HandshakeRequest request) {
    this.session = session;
    this.handshakeRequest = request;
  }

  @Override
  public Optional<Subject> getSubject() {
    return Optional.empty();
  }

  @Override
  public DataLoaderRegistry getDataLoaderRegistry() {
    return dataLoaderRegistry != null ? dataLoaderRegistry : null;
  }

  public void setDataLoaderRegistry(DataLoaderRegistry dataLoaderRegistry) {
    this.dataLoaderRegistry = dataLoaderRegistry;
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

  public void setAuthentication(Authentication authentication) {
    this.authentication = authentication;
    securityContext.setAuthentication(authentication);
  }

  public SecurityContext getSecurityContext() {
    return securityContext;
  }
}
