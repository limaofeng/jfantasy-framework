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
package net.asany.jfantasy.framework.security.oauth2.server.authentication;

import net.asany.jfantasy.autoconfigure.properties.SecurityProperties;
import net.asany.jfantasy.framework.security.AuthenticationManager;
import net.asany.jfantasy.framework.security.DefaultClientDetailsService;
import net.asany.jfantasy.framework.security.InMemoryTokenStore;
import net.asany.jfantasy.framework.security.auth.oauth2.DefaultTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthenticationProvider;
import net.asany.jfantasy.framework.security.web.WebAuthenticationDetailsSource;
import net.asany.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class BearerTokenAuthenticationProviderTest {
  private AuthenticationManager manager = new AuthenticationManager();
  private WebAuthenticationDetailsSource authenticationDetailsSource =
      new WebAuthenticationDetailsSource();

  @BeforeEach
  void setUp() {
    DefaultTokenServices defaultTokenServices =
        new DefaultTokenServices(
            new SecurityProperties(),
            new InMemoryTokenStore(),
            new DefaultClientDetailsService(),
            new ThreadPoolTaskExecutor());
    defaultTokenServices.setTokenStore(new InMemoryTokenStore());
    BearerTokenAuthenticationProvider provider =
        new BearerTokenAuthenticationProvider(defaultTokenServices);
    manager.addProvider(provider);
  }

  @Test
  void authenticate() {
    WebAuthenticationDetails webAuthenticationDetails =
        authenticationDetailsSource.buildDetails(new MockHttpServletRequest());
    BearerTokenAuthenticationToken authentication = new BearerTokenAuthenticationToken("123456");
    authentication.setDetails(webAuthenticationDetails);
    manager.authenticate(authentication);
  }
}
