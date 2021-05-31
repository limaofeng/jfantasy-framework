package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.AuthenticationException;

public class RedirectMismatchException extends AuthenticationException {
    public RedirectMismatchException(String message) {
        super(message);
    }
}
