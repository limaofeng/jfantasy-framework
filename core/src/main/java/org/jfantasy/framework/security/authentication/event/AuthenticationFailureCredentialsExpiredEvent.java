package org.jfantasy.framework.security.authentication.event;

import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.Authentication;

public class AuthenticationFailureCredentialsExpiredEvent extends AbstractAuthenticationFailureEvent {

    public AuthenticationFailureCredentialsExpiredEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }

}