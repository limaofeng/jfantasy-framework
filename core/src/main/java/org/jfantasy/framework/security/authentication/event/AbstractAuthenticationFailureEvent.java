package org.jfantasy.framework.security.authentication.event;

import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.Authentication;
import org.springframework.util.Assert;

public abstract class AbstractAuthenticationFailureEvent extends AbstractAuthenticationEvent {

    private final AuthenticationException exception;

    public AbstractAuthenticationFailureEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication);
        Assert.notNull(exception, "AuthenticationException is required");
        this.exception = exception;
    }

    public AuthenticationException getException() {
        return this.exception;
    }

}
