package org.jfantasy.framework.security.authentication.event;

import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationFailureExpiredEvent extends AbstractAuthenticationFailureEvent {

    public AuthenticationFailureExpiredEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }

}
