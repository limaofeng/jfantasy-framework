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
package net.asany.jfantasy.framework.security.authentication;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 用户名密码认证令牌
 *
 * @author limaofeng
 */
public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken<String> {

  @Getter private final String tenantId;
  private final Object principal;
  private final Object credentials;

  public UsernamePasswordAuthenticationToken(
      String tenantId, Object principal, Object credentials) {
    super(Collections.emptyList());
    this.tenantId = tenantId;
    this.principal = principal;
    this.credentials = credentials;
    setAuthenticated(false);
  }

  public UsernamePasswordAuthenticationToken(
      String tenantId, Object principal, Object credentials, AuthenticationDetails details) {
    this(tenantId, principal, credentials);
    this.details = details;
  }

  public UsernamePasswordAuthenticationToken(
      String tenantId,
      Object principal,
      Object credentials,
      Collection<GrantedAuthority> authorities) {
    super(authorities);
    this.tenantId = tenantId;
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true);
  }

  @Override
  public String getName() {
    if (this.getPrincipal() instanceof LoginUser) {
      return ((LoginUser) this.getPrincipal()).getName();
    }
    if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
      return ((AuthenticatedPrincipal) this.getPrincipal()).getName();
    }
    if (this.getPrincipal() instanceof Principal) {
      return ((Principal) this.getPrincipal()).getName();
    }
    return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
  }

  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }
}
