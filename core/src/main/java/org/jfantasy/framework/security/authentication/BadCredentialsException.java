package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationException;

public class BadCredentialsException extends AuthenticationException {

    public BadCredentialsException(String message) {
        super(message);
    }

}
