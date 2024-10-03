/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
