package org.jfantasy.framework.security;

import org.apache.commons.collections.map.HashedMap;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.core.OAuth2RefreshToken;
import org.jfantasy.framework.security.oauth2.core.TokenStore;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;

import java.util.Collection;
import java.util.Map;

public class InMemoryTokenStore implements TokenStore {

    private Map<String, BearerTokenAuthentication> cache = new HashedMap();

    @Override
    public BearerTokenAuthentication readAuthentication(BearerTokenAuthenticationToken token) {
        return null;
    }

    @Override
    public BearerTokenAuthentication readAuthentication(String token) {
        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, Authentication authentication) {

    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {

    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, BearerTokenAuthentication authentication) {

    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return null;
    }

    @Override
    public BearerTokenAuthentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {

    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {

    }

    @Override
    public OAuth2AccessToken getAccessToken(BearerTokenAuthentication authentication) {
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return null;
    }
}
