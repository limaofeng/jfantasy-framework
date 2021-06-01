package org.jfantasy.framework.security.oauth2.core;

import org.jfantasy.framework.security.AuthenticationException;

public class ClientRegistrationException extends AuthenticationException {

    public ClientRegistrationException(String msg) {
        super(msg);
    }

}
