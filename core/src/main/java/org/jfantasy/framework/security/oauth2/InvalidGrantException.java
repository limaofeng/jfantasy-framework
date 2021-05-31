package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.AuthenticationException;

public class InvalidGrantException extends AuthenticationException {
    public InvalidGrantException(String message) {
        super(message);
    }
}
