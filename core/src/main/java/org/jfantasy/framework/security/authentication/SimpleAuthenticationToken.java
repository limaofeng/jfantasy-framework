package org.jfantasy.framework.security.authentication;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * @author limaofeng
 */
public class SimpleAuthenticationToken<T extends Object> extends AbstractAuthenticationToken {

    private T credentials;

    public SimpleAuthenticationToken(T credentials) {
        super(null);
        this.credentials = credentials;
        setAuthenticated(false);
    }

    @Override
    public T getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
