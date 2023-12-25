package net.asany.jfantasy.framework.security;

import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import net.asany.jfantasy.framework.security.crypto.password.PlaintextPasswordEncoder;
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
