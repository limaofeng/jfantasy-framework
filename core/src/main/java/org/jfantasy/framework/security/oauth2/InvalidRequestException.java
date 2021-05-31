package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.AuthenticationException;

public class InvalidRequestException extends AuthenticationException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
