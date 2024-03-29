package org.jfantasy.framework.security.oauth2.server.web;

import java.util.Map;
import java.util.regex.Matcher;
import javax.websocket.Session;
import org.jfantasy.framework.security.oauth2.core.OAuth2AuthenticationException;
import org.jfantasy.framework.security.oauth2.server.BearerTokenError;
import org.jfantasy.framework.security.oauth2.server.BearerTokenErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * WebSocket 请求头中获取 BearerToken
 *
 * @author limaofeng
 */
public class WebSocketBearerTokenResolver implements BearerTokenResolver<Session> {
  @Override
  public String resolve(Session session) {
    return resolveFromAuthorizationHeader(session);
  }

  private String resolveFromAuthorizationHeader(Session session) {
    Map<String, Object> connectionParams =
        (Map<String, Object>) session.getUserProperties().get("connectionParams");
    String authorization = (String) connectionParams.get(HttpHeaders.AUTHORIZATION.toLowerCase());
    if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
      return null;
    }
    Matcher matcher = DefaultBearerTokenResolver.AUTHORIZATION_PATTERN.matcher(authorization);
    if (!matcher.matches()) {
      BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
      throw new OAuth2AuthenticationException(error);
    }
    return matcher.group("token");
  }
}
