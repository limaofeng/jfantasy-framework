package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import org.springframework.util.Assert;

public abstract class AbstractAuthenticationFailureEvent extends AbstractAuthenticationEvent {

  private final AuthenticationException exception;

  public AbstractAuthenticationFailureEvent(
      Authentication authentication, AuthenticationException exception) {
    super(authentication);
    Assert.notNull(exception, "AuthenticationException is required");
    this.exception = exception;
  }

  public AuthenticationException getException() {
    return this.exception;
  }
}
