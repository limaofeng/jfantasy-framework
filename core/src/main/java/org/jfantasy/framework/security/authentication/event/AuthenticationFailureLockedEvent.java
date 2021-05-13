package org.jfantasy.framework.security.authentication.event;

import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationFailureLockedEvent extends AbstractAuthenticationFailureEvent {

    public AuthenticationFailureLockedEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }

}