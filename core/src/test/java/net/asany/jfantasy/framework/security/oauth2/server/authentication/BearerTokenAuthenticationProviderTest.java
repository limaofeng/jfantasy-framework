package net.asany.jfantasy.framework.security.oauth2.server.authentication;

import net.asany.jfantasy.framework.security.AuthenticationManager;
import net.asany.jfantasy.framework.security.DefaultClientDetailsService;
import net.asany.jfantasy.framework.security.InMemoryTokenStore;
import net.asany.jfantasy.framework.security.oauth2.DefaultTokenServices;
import net.asany.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
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
