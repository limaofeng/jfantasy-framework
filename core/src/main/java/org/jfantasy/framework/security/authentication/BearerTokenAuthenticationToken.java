package org.jfantasy.framework.security.authentication;

import java.util.Collection;

public class BearerTokenAuthenticationToken extends AbstractAuthenticationToken {

    public BearerTokenAuthenticationToken(Collection authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
