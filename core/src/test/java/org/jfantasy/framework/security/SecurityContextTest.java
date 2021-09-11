package org.jfantasy.framework.security;

import static org.junit.jupiter.api.Assertions.*;

import org.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.Test;

class SecurityContextTest {

  @Test
  void isAuthenticated() {
    SecurityContext context = new SecurityContext();
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null, null);
    assert !context.isAuthenticated();
    context.setAuthentication(token);
    token.setAuthenticated(true);
    assert context.isAuthenticated();
  }
}
