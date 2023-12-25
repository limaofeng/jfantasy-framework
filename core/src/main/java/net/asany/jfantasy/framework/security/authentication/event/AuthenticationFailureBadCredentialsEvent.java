package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationFailureBadCredentialsEvent extends AbstractAuthenticationFailureEvent {

  public AuthenticationFailureBadCredentialsEvent(
      Authentication authentication, AuthenticationException exception) {
    super(authentication, exception);
  }
}
