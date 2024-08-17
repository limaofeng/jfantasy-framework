package net.asany.jfantasy.framework.security.auth.oauth2.server.web;

import jakarta.websocket.Session;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.asany.jfantasy.framework.security.auth.AuthTokenType;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationTokenException;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenError;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenErrors;
import org.springframework.http.HttpHeaders;

/**
 * WebSocket 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class WebSocketBearerTokenResolver implements BearerTokenResolver<Session> {

  protected Pattern AUTHORIZATION_PATTERN =
      Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

  @Override
  public boolean supports(Session request) {
    String authorization = getAuthorization(request);
    Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
    return matcher.matches();
  }

  @Override
  public String resolve(Session session) {
    return resolveFromAuthorizationHeader(session);
  }

  @Override
  public AuthTokenType getAuthTokenType() {
    return AuthTokenType.ACCESS_TOKEN;
  }

  private String getAuthorization(Session session) {
    Map<String, Object> connectionParams =
        (Map<String, Object>) session.getUserProperties().get("connectionParams");
    return (String) connectionParams.get(HttpHeaders.AUTHORIZATION.toLowerCase());
  }

  private String resolveFromAuthorizationHeader(Session session) {
    String authorization = getAuthorization(session);
    Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
    if (!matcher.matches()) {
      BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
      throw new AuthenticationTokenException(error);
    }
    return matcher.group("token");
  }
}
