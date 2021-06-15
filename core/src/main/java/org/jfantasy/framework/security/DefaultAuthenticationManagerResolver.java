package org.jfantasy.framework.security;

import org.jfantasy.framework.security.authentication.AuthenticationManagerResolver;

/**
 * @author limaofeng
 */
public class DefaultAuthenticationManagerResolver implements AuthenticationManagerResolver {

    private AuthenticationManager authenticationManager;

    public DefaultAuthenticationManagerResolver(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationManager resolve(Object context) {
        return this.authenticationManager;
    }

}
