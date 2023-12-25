package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationFailureCredentialsExpiredEvent
    extends AbstractAuthenticationFailureEvent {

  public AuthenticationFailureCredentialsExpiredEvent(
      Authentication authentication, AuthenticationException exception) {
    super(authentication, exception);
  }
}
