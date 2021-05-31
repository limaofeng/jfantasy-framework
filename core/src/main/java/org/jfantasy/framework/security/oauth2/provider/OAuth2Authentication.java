package org.jfantasy.framework.security.oauth2.provider;

import org.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import org.jfantasy.framework.security.authentication.Authentication;

public class OAuth2Authentication extends AbstractAuthenticationToken {

    private final OAuth2Request storedRequest;
    private final Authentication userAuthentication;

    public OAuth2Authentication(OAuth2Request storedRequest, Authentication userAuthentication) {
        super(userAuthentication == null ? storedRequest.getAuthorities() : userAuthentication.getAuthorities());
        this.storedRequest = storedRequest;
        this.userAuthentication = userAuthentication;
    }

    public Object getCredentials() {
        return "";
    }

    public Object getPrincipal() {
        return this.userAuthentication == null ? this.storedRequest.getClientId() : this.userAuthentication.getPrincipal();
    }

    public boolean isClientOnly() {
        return userAuthentication == null;
    }

    public OAuth2Request getOAuth2Request() {
        return storedRequest;
    }

    public Authentication getUserAuthentication() {
        return userAuthentication;
    }

    @Override
    public boolean isAuthenticated() {
        return this.storedRequest.isApproved() && (this.userAuthentication == null || this.userAuthentication.isAuthenticated());
    }

}
