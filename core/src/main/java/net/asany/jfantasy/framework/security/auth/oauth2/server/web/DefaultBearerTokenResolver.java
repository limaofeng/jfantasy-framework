package net.asany.jfantasy.framework.security.auth.oauth2.server.web;

import jakarta.servlet.http.HttpServletRequest;
import net.asany.jfantasy.framework.security.auth.AuthTokenType;

/**
 * Http 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class DefaultBearerTokenResolver extends AbstractBearerTokenResolver<HttpServletRequest> {
  @Override
  public String getHeader(HttpServletRequest request, String name) {
    return request.getHeader(name);
  }

  @Override
  public String[] getParameterValues(HttpServletRequest request, String name) {
    return request.getParameterValues(name);
  }

  @Override
  public String getRequestMethod(HttpServletRequest request) {
    return request.getMethod();
  }

  @Override
  public AuthTokenType getAuthTokenType() {
    return AuthTokenType.ACCESS_TOKEN;
  }
}
