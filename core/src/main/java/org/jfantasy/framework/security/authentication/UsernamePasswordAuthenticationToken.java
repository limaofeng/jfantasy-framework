package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.core.AuthenticatedPrincipal;
import org.jfantasy.framework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

/**
 * @author limaofeng
 */
public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Object credentials;

    public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public String getName() {
        if (this.getPrincipal() instanceof LoginUser) {
            return ((LoginUser) this.getPrincipal()).getName();
        }
        if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
            return ((AuthenticatedPrincipal) this.getPrincipal()).getName();
        }
        if (this.getPrincipal() instanceof Principal) {
            return ((Principal) this.getPrincipal()).getName();
        }
        return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
