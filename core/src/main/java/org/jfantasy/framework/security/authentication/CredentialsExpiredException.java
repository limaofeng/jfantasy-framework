package org.jfantasy.framework.security.authentication;

public class CredentialsExpiredException extends AccountStatusException {
    public CredentialsExpiredException(String msg) {
        super(msg);
    }

}
