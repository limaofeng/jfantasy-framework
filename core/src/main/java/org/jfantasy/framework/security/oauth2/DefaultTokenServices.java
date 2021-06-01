package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.oauth2.core.ClientDetailsService;
import org.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.core.TokenStore;
import org.jfantasy.framework.security.oauth2.core.token.AuthorizationServerTokenServices;
import org.jfantasy.framework.security.oauth2.core.token.ConsumerTokenServices;
import org.jfantasy.framework.security.oauth2.core.token.ResourceServerTokenServices;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;
import org.springframework.beans.factory.InitializingBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author limaofeng
 */
public class DefaultTokenServices implements AuthorizationServerTokenServices, ResourceServerTokenServices, ConsumerTokenServices, InitializingBean {

    private boolean supportRefreshToken;
    private TokenStore tokenStore;
    private ClientDetailsService clientDetailsService;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public DefaultTokenServices(TokenStore tokenStore, ClientDetailsService clientDetailsService) {
        this.tokenStore = tokenStore;
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public OAuth2AccessToken createAccessToken(Authentication authentication) {
//        authentication.getOAuth2Request();

        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        this.clientDetailsService.loadClientByClientId(details.getClientId());
        String tokenValue = "";
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, Instant.now(), Instant.now().plus(10, ChronoUnit.MINUTES));
        tokenStore.storeAccessToken(accessToken, authentication);
        return accessToken;
    }

    @Override
    public OAuth2AccessToken getAccessToken(Authentication authentication) {
        return null;
    }

//    @Override
//    public OAuth2AccessToken refreshAccessToken(String refreshToken, TokenRequest tokenRequest) {
//        return null;
//    }

    @Override
    public boolean revokeToken(String tokenValue) {
        return false;
    }

    @Override
    public BearerTokenAuthentication loadAuthentication(String accessToken) {
        this.tokenStore.readAuthentication(accessToken);
        return null;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return null;
    }

    public void setSupportRefreshToken(boolean supportRefreshToken) {
        this.supportRefreshToken = supportRefreshToken;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }
}
