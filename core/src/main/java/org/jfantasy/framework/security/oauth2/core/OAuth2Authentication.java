package org.jfantasy.framework.security.oauth2.core;

import org.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * OAuth 身份验证
 *
 * @author limaofeng
 */
public class OAuth2Authentication extends AbstractAuthenticationToken {

    private OAuth2AuthenticatedPrincipal principal;
    private OAuth2AuthenticationDetails credentials;

    public OAuth2Authentication(Authentication authentication, OAuth2AuthenticationDetails credentials) {
        super(authentication.getAuthorities());
        Assert.isTrue(credentials.getTokenType() == OAuth2AccessToken.TokenType.BEARER, "credentials must be a bearer token");
        this.principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
        this.credentials = credentials;
        setAuthenticated(authentication.isAuthenticated());
    }

    public OAuth2Authentication(OAuth2AuthenticatedPrincipal principal, OAuth2AuthenticationDetails credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.isTrue(credentials.getTokenType() == OAuth2AccessToken.TokenType.BEARER, "credentials must be a bearer token");
        setAuthenticated(true);
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