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
package net.asany.jfantasy.framework.security.auth.base;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.AbstractAuthTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

public class AnonymousAuthentication
    extends AbstractAuthTokenAuthenticationToken<AuthenticatedPrincipal, AuthToken> {
  private final Map<String, Object> attributes;

  public AnonymousAuthentication(
      AuthenticatedPrincipal principal,
      AuthToken credentials,
      Collection<GrantedAuthority> authorities,
      AuthenticationDetails details) {
    super(principal, credentials, authorities);
    this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(principal.getAttributes()));
    setAuthenticated(true);
    this.details = details;
  }

  @Override
  public Map<String, Object> getTokenAttributes() {
    return this.attributes;
  }
}
