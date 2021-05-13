package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationException;

public class ProviderNotFoundException extends AuthenticationException {

    public ProviderNotFoundException(String msg) {
        super(msg);
    }

}