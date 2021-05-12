package org.jfantasy.framework.security.authentication.event;

import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.Authentication;

/**
 * @author limaofeng
 */
public class AuthenticationFailureProviderNotFoundEvent extends AbstractAuthenticationFailureEvent {

    public AuthenticationFailureProviderNotFoundEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }

}