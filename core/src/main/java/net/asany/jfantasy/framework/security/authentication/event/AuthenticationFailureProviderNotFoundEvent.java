package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;

/**
 * @author limaofeng
 */
public class AuthenticationFailureProviderNotFoundEvent extends AbstractAuthenticationFailureEvent {

  public AuthenticationFailureProviderNotFoundEvent(
      Authentication authentication, AuthenticationException exception) {
    super(authentication, exception);
  }
}
