package org.jfantasy.framework.security.authentication;

public class LockedException extends AccountStatusException {

    public LockedException(String msg) {
        super(msg);
    }
}
