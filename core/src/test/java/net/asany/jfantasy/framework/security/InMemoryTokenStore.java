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
package net.asany.jfantasy.framework.security;

import java.util.Collection;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.AuthRefreshToken;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.auth.core.TokenStore;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import org.apache.commons.collections.map.HashedMap;

public class InMemoryTokenStore implements TokenStore<OAuth2AccessToken> {

  private final Map<String, BearerTokenAuthentication> cache = new HashedMap();

  @Override
  public Authentication readAuthentication(AuthenticationToken<String> token) {
    return null;
  }

  @Override
  public AuthenticationToken<OAuth2AccessToken> readAuthentication(String token) {
    return null;
  }

  @Override
  public void storeAccessToken(OAuth2AccessToken token, Authentication authentication) {}

  @Override
  public OAuth2AccessToken readAccessToken(String tokenValue) {
    return null;
  }

  @Override
  public void removeAccessToken(OAuth2AccessToken token) {}

  @Override
  public void storeRefreshToken(AuthRefreshToken refreshToken, Authentication authentication) {}

  @Override
  public AuthRefreshToken readRefreshToken(String tokenValue) {
    return null;
  }

  @Override
  public BearerTokenAuthentication readAuthenticationForRefreshToken(AuthRefreshToken token) {
    return null;
  }

  @Override
  public void removeRefreshToken(AuthRefreshToken token) {}

  @Override
  public void removeAccessTokenUsingRefreshToken(AuthRefreshToken refreshToken) {}

  @Override
  public OAuth2AccessToken getAccessToken(AuthenticationToken authentication) {
    return null;
  }

  @Override
  public Collection<AuthToken> findTokensByClientIdAndUserName(String clientId, String userName) {
    return null;
  }

  @Override
  public Collection<AuthToken> findTokensByClientId(String clientId) {
    return null;
  }
}
