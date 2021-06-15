package org.jfantasy.framework.security.oauth2.server.authentication;

import org.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractOAuth2TokenAuthenticationToken<T extends AbstractOAuth2Token> extends AbstractAuthenticationToken {

    private Object principal;

    private Object credentials;

    private T token;

    protected AbstractOAuth2TokenAuthenticationToken(T token) {
        this(token, null);
    }

    protected AbstractOAuth2TokenAuthenticationToken(T token, Collection<? extends GrantedAuthority> authorities) {
        this(token, token, token, authorities);
    }

    protected AbstractOAuth2TokenAuthenticationToken(T token, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.notNull(token, "token cannot be null");
        Assert.notNull(principal, "principal cannot be null");
        this.principal = principal;
        this.credentials = credentials;
        this.token = token;
    }


    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    public final T getToken() {
        return this.token;
    }

    public abstract Map<String, Object> getTokenAttributes();

}
