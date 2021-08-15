package org.jfantasy.framework.security;

import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import org.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import org.jfantasy.framework.security.crypto.password.PlaintextPasswordEncoder;
import org.junit.jupiter.api.Test;

class AuthenticationManagerTest {

  @Test
  void authenticate() {
    AuthenticationManager manager = new AuthenticationManager();

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(new SimpleUserDetailsService());
    provider.setPasswordEncoder(new PlaintextPasswordEncoder());

    manager.addProvider(provider);

    Authentication authentication =
        new UsernamePasswordAuthenticationToken("limaofeng", "123456789");

    manager.authenticate(authentication);
  }
}
