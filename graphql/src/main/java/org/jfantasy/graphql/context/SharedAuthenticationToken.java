package org.jfantasy.graphql.context;

import org.jfantasy.framework.security.authentication.SimpleAuthenticationToken;

/**
 * 用于 GraphQL Token
 * @author limaofeng
 */
public class SharedAuthenticationToken extends SimpleAuthenticationToken<String> {

    public SharedAuthenticationToken(String credentials) {
        super(credentials);
    }

}
