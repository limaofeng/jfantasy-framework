package org.jfantasy.framework.security.oauth2.server.web;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jfantasy.framework.security.oauth2.core.OAuth2AuthenticationException;
import org.jfantasy.framework.security.oauth2.server.BearerTokenError;
import org.jfantasy.framework.security.oauth2.server.BearerTokenErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

/**
 * Http 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class WebFluxBearerTokenResolver implements BearerTokenResolver<ServerHttpRequest> {

  protected static final Pattern AUTHORIZATION_PATTERN =
      Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

  private boolean allowFormEncodedBodyParameter = false;

  private boolean allowUriQueryParameter = false;

  private String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

  @Override
  public String resolve(ServerHttpRequest request) {
    String authorizationHeaderToken = resolveFromAuthorizationHeader(request);
    String parameterToken = resolveFromRequestParameters(request);
    if (authorizationHeaderToken != null) {
      if (parameterToken != null) {
        BearerTokenError error =
            BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
        throw new OAuth2AuthenticationException(error);
      }
      return authorizationHeaderToken;
    }
    if (parameterToken != null && isParameterTokenSupportedForRequest(request)) {
      return parameterToken;
    }
    return null;
  }

  public void setAllowFormEncodedBodyParameter(boolean allowFormEncodedBodyParameter) {
    this.allowFormEncodedBodyParameter = allowFormEncodedBodyParameter;
  }

  public void setAllowUriQueryParameter(boolean allowUriQueryParameter) {
    this.allowUriQueryParameter = allowUriQueryParameter;
  }

  public void setBearerTokenHeaderName(String bearerTokenHeaderName) {
    this.bearerTokenHeaderName = bearerTokenHeaderName;
  }

  private String resolveFromAuthorizationHeader(ServerHttpRequest request) {
    String authorization = request.getHeaders().getFirst(this.bearerTokenHeaderName);
    if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
      return null;
    }
    Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
    if (!matcher.matches()) {
      BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
      throw new OAuth2AuthenticationException(error);
    }
    return matcher.group("token");
  }

  private static String resolveFromRequestParameters(ServerHttpRequest request) {
    List<String> values = request.getQueryParams().get("access_token");
    if (values == null || values.isEmpty()) {
      return null;
    }
    if (values.size() == 1) {
      return values.get(0);
    }
    BearerTokenError error =
        BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
    throw new OAuth2AuthenticationException(error);
  }

  private boolean isParameterTokenSupportedForRequest(ServerHttpRequest request) {
    return ((this.allowFormEncodedBodyParameter && "POST".equals(request.getMethod()))
        || (this.allowUriQueryParameter && "GET".equals(request.getMethod())));
  }
}
