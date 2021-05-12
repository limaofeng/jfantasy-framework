package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationException;

public abstract class AccountStatusException extends AuthenticationException {

    public AccountStatusException(String msg) {
        super(msg);
    }

    public AccountStatusException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
