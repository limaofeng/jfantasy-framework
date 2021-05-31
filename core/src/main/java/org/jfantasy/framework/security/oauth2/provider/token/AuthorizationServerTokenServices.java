package org.jfantasy.framework.security.oauth2.provider.token;

import org.jfantasy.framework.security.oauth2.provider.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;

public interface AuthorizationServerTokenServices {

    OAuth2AccessToken createAccessToken(OAuth2Authentication authentication);

    OAuth2AccessToken getAccessToken(OAuth2Authentication authentication);

    OAuth2AccessToken refreshAccessToken(String refreshToken, TokenRequest tokenRequest);

}
