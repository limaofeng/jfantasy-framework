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

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.auth.core.InvalidTokenException;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.springframework.core.annotation.Order;

/**
 * @author limaofeng
 */
@Slf4j
@Order(100)
public class BearerTokenAuthenticationProvider
    implements AuthenticationProvider<BearerTokenAuthenticationToken> {

  private final ResourceServerTokenServices<OAuth2AccessToken> tokenServices;

  public BearerTokenAuthenticationProvider(
      ResourceServerTokenServices<OAuth2AccessToken> tokenServices) {
    this.tokenServices = tokenServices;
  }

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(BearerTokenAuthenticationToken bearer)
      throws AuthenticationException {
    Authentication authentication = this.tokenServices.loadAuthentication(bearer);
    if (authentication == null) {
      throw new InvalidTokenException("Invalid token");
    }
    return authentication;
  }
}
