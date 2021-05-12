package org.jfantasy.framework.security.authentication;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * @author limaofeng
 */
public class SimpleAuthenticationToken extends AbstractAuthenticationToken {

    private Object credentials;

    public SimpleAuthenticationToken(Object credentials) {
        super(null);
        this.credentials = credentials;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
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
