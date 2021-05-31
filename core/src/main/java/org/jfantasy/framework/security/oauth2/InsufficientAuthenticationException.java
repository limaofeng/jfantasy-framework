package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.AuthenticationException;

public class InsufficientAuthenticationException  extends AuthenticationException {
    public InsufficientAuthenticationException(String message) {
        super(message);
    }
}
