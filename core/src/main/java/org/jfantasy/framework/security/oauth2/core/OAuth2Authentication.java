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
    private Object credentials;

    public OAuth2Authentication(Authentication authentication, OAuth2AuthenticationDetails details) {
        super(authentication.getAuthorities());
        Assert.isTrue(details.getTokenType() == OAuth2AccessToken.TokenType.BEARER, "credentials must be a bearer token");
        this.credentials = authentication.getCredentials();
        this.principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
        setDetails(details);
        setAuthenticated(authentication.isAuthenticated());
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