package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.oauth2.provider.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;
import org.jfantasy.framework.security.oauth2.provider.OAuth2RefreshToken;

import java.util.Collection;

public interface TokenStore {

    OAuth2Authentication readAuthentication(OAuth2AccessToken token);

    OAuth2Authentication readAuthentication(String token);

    void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication);

    OAuth2AccessToken readAccessToken(String tokenValue);

    void removeAccessToken(OAuth2AccessToken token);

    void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication);

    OAuth2RefreshToken readRefreshToken(String tokenValue);

    OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token);

    void removeRefreshToken(OAuth2RefreshToken token);

    void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken);

    OAuth2AccessToken getAccessToken(OAuth2Authentication authentication);

    Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName);

    Collection<OAuth2AccessToken> findTokensByClientId(String clientId);
}
