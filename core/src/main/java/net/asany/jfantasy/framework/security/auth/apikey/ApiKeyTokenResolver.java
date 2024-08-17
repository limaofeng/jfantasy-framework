package net.asany.jfantasy.framework.security.auth.apikey;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import net.asany.jfantasy.framework.security.auth.AuthTokenType;
import net.asany.jfantasy.framework.security.auth.oauth2.server.web.AbstractBearerTokenResolver;

/**
 * Http 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class ApiKeyTokenResolver extends AbstractBearerTokenResolver<HttpServletRequest> {

  public ApiKeyTokenResolver() {
    this.AUTHORIZATION_PATTERN =
        Pattern.compile("^Bearer ak-(?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);
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
    return AuthTokenType.API_KEY;
  }
}
