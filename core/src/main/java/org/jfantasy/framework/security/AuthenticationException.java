package org.jfantasy.framework.security;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
