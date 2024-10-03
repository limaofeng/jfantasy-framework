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
package net.asany.jfantasy.framework.security.auth.oauth2.token;

import java.util.Collections;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;

/**
 * 客户端凭证认证
 *
 * @since 1.0
 */
public class ClientCredentialsAuthenticationToken extends AbstractAuthenticationToken<String> {

  private final String principal;
  private final String credentials;

  public ClientCredentialsAuthenticationToken(
      String principal, String credentials, AuthenticationDetails details) {
    super(Collections.emptyList());
    this.principal = principal;
    this.credentials = credentials;
    this.setDetails(details);
    setAuthenticated(false);
  }

  @Override
  public String getCredentials() {
    return this.credentials;
  }

  @Override
  public String getPrincipal() {
    return this.principal;
  }
}
