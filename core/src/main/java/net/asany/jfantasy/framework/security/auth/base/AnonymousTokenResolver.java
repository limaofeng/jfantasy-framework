package net.asany.jfantasy.framework.security.auth.base;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import net.asany.jfantasy.framework.security.auth.AuthTokenType;
import net.asany.jfantasy.framework.security.auth.oauth2.server.web.AbstractBearerTokenResolver;

/**
 * Http 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class AnonymousTokenResolver extends AbstractBearerTokenResolver<HttpServletRequest> {

  public AnonymousTokenResolver() {
    this.AUTHORIZATION_PATTERN =
        Pattern.compile("^Bearer an-(?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);
  }

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
    return AuthTokenType.ANONYMOUS;
  }
}
