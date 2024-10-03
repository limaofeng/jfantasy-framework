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
package net.asany.jfantasy.framework.security.auth.oauth2.provider;

import java.util.ArrayList;
import java.util.Optional;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.auth.core.AuthorizationGrantType;
import net.asany.jfantasy.framework.security.auth.core.ClientSecret;
import net.asany.jfantasy.framework.security.auth.core.ClientSecretType;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2Authentication;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2Token;
import net.asany.jfantasy.framework.security.auth.oauth2.token.ClientCredentialsAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.AuthenticationProvider;
import net.asany.jfantasy.framework.security.core.user.ClientApp;

public class ClientCredentialsAuthenticationProvider
    implements AuthenticationProvider<ClientCredentialsAuthenticationToken> {
  @Override
  public boolean supports(Class<? extends Authentication> authentication) {
    return ClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Override
  public Authentication authenticate(ClientCredentialsAuthenticationToken authentication)
      throws AuthenticationException {
    String clientSecret = authentication.getCredentials();

    AuthenticationDetails details = authentication.getDetails();

    if (details.getClientDetails() == null) {
      throw new AuthenticationException("Invalid client details");
    }

    Optional<ClientSecret> secretOptional =
        details.getClientDetails().getClientSecrets(ClientSecretType.CLIENT_CERTIFICATE).stream()
            .filter(s -> s.getSecretValue().equals(clientSecret))
            .findFirst();

    if (secretOptional.isEmpty()) {
      throw new AuthenticationException("Invalid client secret");
    }

    details.setClientSecret(secretOptional.get());

    return new OAuth2Authentication(
        AuthorizationGrantType.CLIENT_CREDENTIALS,
        new ClientApp(details.getClientDetails()),
        new OAuth2Token(secretOptional.get()),
        new ArrayList<>(),
        details);
  }
}
