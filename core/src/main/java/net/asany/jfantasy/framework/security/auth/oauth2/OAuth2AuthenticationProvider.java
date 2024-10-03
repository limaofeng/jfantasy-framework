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
package net.asany.jfantasy.framework.security.auth.oauth2;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.AuthenticationManager;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.AuthorizationGrantType;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.token.ClientCredentialsAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationProvider;
import net.asany.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;

public class OAuth2AuthenticationProvider
    implements AuthenticationProvider<OAuth2AuthenticationToken> {

  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(OAuth2AuthenticationToken authentication)
      throws AuthenticationException {
    AuthenticationManager authenticationManager =
        SpringBeanUtils.getBean(AuthenticationManager.class);

    AuthorizationGrantType grantType = authentication.getGrantType();

    if (AuthorizationGrantType.PASSWORD == grantType) {
      AuthenticationToken<String> authenticationToken =
          new UsernamePasswordAuthenticationToken(
              authentication.getPrincipal(),
              authentication.getCredentials(),
              authentication.getDetails());
      return authenticationManager.authenticate(authenticationToken);
    } else if (AuthorizationGrantType.REFRESH_TOKEN == grantType) {
      AuthenticationToken<String> authenticationToken =
          new UsernamePasswordAuthenticationToken(
              authentication.getPrincipal(),
              authentication.getCredentials(),
              authentication.getDetails());
      return authenticationManager.authenticate(authenticationToken);
    } else if (AuthorizationGrantType.CLIENT_CREDENTIALS == grantType) {
      AuthenticationToken<String> authenticationToken =
          new ClientCredentialsAuthenticationToken(
              authentication.getPrincipal(),
              (String) authentication.getCredentials(),
              authentication.getDetails());
      return authenticationManager.authenticate(authenticationToken);
    }

    throw new AuthenticationException("Unsupported grant type: " + grantType.toString());
  }
}
