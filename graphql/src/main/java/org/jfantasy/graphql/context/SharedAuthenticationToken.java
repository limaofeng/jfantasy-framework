package org.jfantasy.graphql.context;

import org.jfantasy.framework.security.authentication.SimpleAuthenticationToken;

public class SharedAuthenticationToken extends SimpleAuthenticationToken {

    public SharedAuthenticationToken(Object credentials) {
        super(credentials);
    }

}
