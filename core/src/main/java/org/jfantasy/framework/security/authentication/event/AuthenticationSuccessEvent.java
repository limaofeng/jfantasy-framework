package org.jfantasy.framework.security.authentication.event;

import org.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationSuccessEvent extends AbstractAuthenticationEvent {

  public AuthenticationSuccessEvent(Authentication authentication) {
    super(authentication);
  }
}
