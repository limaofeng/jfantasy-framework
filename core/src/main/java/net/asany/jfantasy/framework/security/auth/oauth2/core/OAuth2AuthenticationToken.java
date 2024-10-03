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
package net.asany.jfantasy.framework.security.auth.oauth2.core;

import java.util.Collections;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.core.AuthorizationGrantType;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;

/**
 * OAuth 身份验证
 *
 * <p>用于 OAuth2 认证的身份验证令牌≤/p>
 */
@Getter
public class OAuth2AuthenticationToken extends AbstractAuthenticationToken<String> {

  private final String principal;
  private final Object credentials;
  private final AuthorizationGrantType grantType;

  public OAuth2AuthenticationToken(
      AuthorizationGrantType grantType,
      String principal,
      String credentials,
      OAuth2AuthenticationDetails details) {
    super(Collections.emptyList());
    this.grantType = grantType;
    this.principal = principal;
    this.credentials = credentials;
    setDetails(details);
    setAuthenticated(false);
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public <T> T getPrincipal() {
    return (T) this.principal;
  }
}
