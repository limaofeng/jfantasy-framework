package net.asany.jfantasy.framework.security.oauth2.server.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.asany.jfantasy.framework.security.oauth2.core.OAuth2AuthenticationException;
import net.asany.jfantasy.framework.security.oauth2.server.BearerTokenError;
import net.asany.jfantasy.framework.security.oauth2.server.BearerTokenErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * Http 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class DefaultBearerTokenResolver implements BearerTokenResolver<HttpServletRequest> {

  protected static final Pattern AUTHORIZATION_PATTERN =
      Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

  private boolean allowFormEncodedBodyParameter = false;

  private boolean allowUriQueryParameter = false;

  private String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

  @Override
  public String resolve(HttpServletRequest request) {
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

  private String resolveFromAuthorizationHeader(HttpServletRequest request) {
    String authorization = request.getHeader(this.bearerTokenHeaderName);
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

  private static String resolveFromRequestParameters(HttpServletRequest request) {
    String[] values = request.getParameterValues("access_token");
    if (values == null || values.length == 0) {
      return null;
    }
    if (values.length == 1) {
      return values[0];
    }
    BearerTokenError error =
        BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
    throw new OAuth2AuthenticationException(error);
  }

  private boolean isParameterTokenSupportedForRequest(HttpServletRequest request) {
    return ((this.allowFormEncodedBodyParameter && "POST".equals(request.getMethod()))
        || (this.allowUriQueryParameter && "GET".equals(request.getMethod())));
  }
}
