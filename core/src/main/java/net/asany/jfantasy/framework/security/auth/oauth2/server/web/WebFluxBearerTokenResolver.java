package net.asany.jfantasy.framework.security.auth.oauth2.server.web;

import net.asany.jfantasy.framework.security.auth.AuthTokenType;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Http 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class WebFluxBearerTokenResolver extends AbstractBearerTokenResolver<ServerHttpRequest> {

  @Override
  public String getHeader(ServerHttpRequest request, String name) {
    return request.getHeaders().getFirst(name);
  }

  @Override
  public String[] getParameterValues(ServerHttpRequest request, String name) {
    return request.getQueryParams().get(name).toArray(String[]::new);
  }

  @Override
  public String getRequestMethod(ServerHttpRequest request) {
    return request.getMethod().name();
  }

  @Override
  public AuthTokenType getAuthTokenType() {
    return AuthTokenType.ACCESS_TOKEN;
  }
}
