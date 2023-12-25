package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationSuccessEvent extends AbstractAuthenticationEvent {

  public AuthenticationSuccessEvent(Authentication authentication) {
    super(authentication);
  }
}
