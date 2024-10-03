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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.auth.core.ClientDetails;
import net.asany.jfantasy.framework.security.auth.core.ClientDetailsService;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationProvider;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.RoleAuthority;

public class AnonymousAuthenticationProvider
    implements AuthenticationProvider<AnonymousAuthenticationToken> {

  private final ClientDetailsService clientDetailsService;

  public AnonymousAuthenticationProvider(ClientDetailsService clientDetailsService) {
    this.clientDetailsService = clientDetailsService;
  }

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return AnonymousAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(AnonymousAuthenticationToken authentication)
      throws AuthenticationException {
    String credentials = authentication.getCredentials();
    String clientId = new String(Base64.getDecoder().decode(credentials));
    ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new RoleAuthority("ANONYMOUS"));
    return new AnonymousAuthentication(
        new AnonymousUser(clientDetails),
        new AnonymousToken(clientId, "an-" + credentials),
        authorities,
        authentication.getDetails());
  }
}
