package net.asany.jfantasy.framework.security.auth.oauth2.server.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Setter;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationTokenException;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenError;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenErrors;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.http.HttpHeaders;

@Setter
public abstract class AbstractBearerTokenResolver<T> implements BearerTokenResolver<T> {

  protected Pattern AUTHORIZATION_PATTERN =
      Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

  protected boolean allowFormEncodedBodyParameter = false;

  protected boolean allowUriQueryParameter = false;

  protected String tokenHeaderName = HttpHeaders.AUTHORIZATION;

  @Override
  public boolean supports(T request) {
    String authorization = getHeader(request, this.tokenHeaderName);
    return authorization != null && AUTHORIZATION_PATTERN.matcher(authorization).matches();
  }

  @Override
  public String resolve(T request) {
    String authorizationHeaderToken = resolveFromAuthorizationHeader(request);

    if (!isParameterTokenSupportedForRequest(request)) {
      return authorizationHeaderToken;
    }

    String parameterToken = resolveFromRequestParameters(request);
    if (authorizationHeaderToken != null && parameterToken != null) {
      BearerTokenError error =
          BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
      throw new AuthenticationTokenException(error);
    }
    return ObjectUtil.defaultValue(authorizationHeaderToken, parameterToken);
  }

  public abstract String getHeader(T request, String name);

  public abstract String[] getParameterValues(T request, String name);

  protected String resolveFromAuthorizationHeader(T request) {
    String authorization = getHeader(request, this.tokenHeaderName);
    Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
    if (!matcher.matches()) {
      BearerTokenError error = BearerTokenErrors.invalidToken("token is malformed");
      throw new AuthenticationTokenException(error);
    }
    return matcher.group("token");
  }

  private String resolveFromRequestParameters(T request) {
    String[] values = getParameterValues(request, "access_token");
    if (values == null || values.length == 0) {
      return null;
    }
    if (values.length == 1) {
      return values[0];
    }
    BearerTokenError error =
        BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
    throw new AuthenticationTokenException(error);
  }

  public abstract String getRequestMethod(T request);

  private boolean isParameterTokenSupportedForRequest(T request) {
    return ((this.allowFormEncodedBodyParameter && "POST".equals(getRequestMethod(request)))
        || (this.allowUriQueryParameter && "GET".equals(getRequestMethod(request))));
  }
}
