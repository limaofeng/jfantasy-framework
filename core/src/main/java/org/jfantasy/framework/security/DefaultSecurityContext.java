package org.jfantasy.framework.security;

import org.springframework.boot.actuate.endpoint.SecurityContext;

import java.security.Principal;
import java.util.ArrayList;

/**
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2019-04-19 14:32
 */
public class DefaultSecurityContext implements SecurityContext {
    private LoginUser user;
    private Object details;

    public DefaultSecurityContext(LoginUser user) {
        this.user = user;
    }

    public DefaultSecurityContext(LoginUser user, Object details) {
        this.user = user;
        this.details = details;
    }

    @Override
    public Principal getPrincipal() {
        return this.user;
    }

    @Override
    public boolean isUserInRole(String role) {
        if (this.user.getAuthoritys() == null) {
            this.user.setAuthoritys(new ArrayList<>());
        }
        return this.user.getAuthoritys().stream().anyMatch(authority -> authority.equals(role));
    }
}
