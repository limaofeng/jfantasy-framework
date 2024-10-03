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
package net.asany.jfantasy.framework.security.auth.oauth2.server.authentication;

import java.util.Collection;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public abstract class AbstractAuthTokenAuthenticationToken<P, C>
    extends AbstractAuthenticationToken<AuthToken> {

  private final P principal;

  private final C credentials;

  protected AbstractAuthTokenAuthenticationToken(
      P principal, C credentials, Collection<GrantedAuthority> authorities) {
    super(authorities);
    Assert.notNull(principal, "principal cannot be null");
    this.principal = principal;
    this.credentials = credentials;
  }

  @Override
  public P getPrincipal() {
    return this.principal;
  }

  @Override
  public C getCredentials() {
    return this.credentials;
  }

  public abstract Map<String, Object> getTokenAttributes();
}
