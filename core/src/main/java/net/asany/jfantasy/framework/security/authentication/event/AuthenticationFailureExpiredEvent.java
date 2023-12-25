package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationFailureExpiredEvent extends AbstractAuthenticationFailureEvent {

  public AuthenticationFailureExpiredEvent(
      Authentication authentication, AuthenticationException exception) {
    super(authentication, exception);
  }
}
