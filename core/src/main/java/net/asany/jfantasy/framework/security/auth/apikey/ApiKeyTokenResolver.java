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
