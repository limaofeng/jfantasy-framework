package org.jfantasy.framework.security.authentication.event;

import org.jfantasy.framework.security.authentication.Authentication;

/** @author limaofeng */
public class LogoutSuccessEvent extends AbstractAuthenticationEvent {

  public LogoutSuccessEvent(Authentication authentication) {
    super(authentication);
  }
}
