package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.AuthenticationException;

public class InvalidClientException extends AuthenticationException {
    public InvalidClientException(String message) {
        super(message);
    }
}
