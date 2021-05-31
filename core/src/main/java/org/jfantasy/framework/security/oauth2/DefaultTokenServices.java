package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.oauth2.provider.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;
import org.jfantasy.framework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.jfantasy.framework.security.oauth2.provider.token.ConsumerTokenServices;
import org.jfantasy.framework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.beans.factory.InitializingBean;

public class DefaultTokenServices implements AuthorizationServerTokenServices, ResourceServerTokenServices, ConsumerTokenServices, InitializingBean {
    private boolean supportRefreshToken;
    private TokenStore tokenStore;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) {
        return null;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return null;
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshToken, TokenRequest tokenRequest) {
        return null;
    }

    @Override
    public boolean revokeToken(String tokenValue) {
        return false;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) {
        return null;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return null;
    }

}
