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
