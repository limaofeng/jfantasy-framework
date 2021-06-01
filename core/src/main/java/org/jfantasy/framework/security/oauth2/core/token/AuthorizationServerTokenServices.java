package org.jfantasy.framework.security.oauth2.core.token;

import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;

/**
 * @author limaofeng
 */
public interface AuthorizationServerTokenServices {

    OAuth2AccessToken createAccessToken(Authentication authentication);

    OAuth2AccessToken getAccessToken(Authentication authentication);

//    OAuth2AccessToken refreshAccessToken(String refreshToken, TokenRequest tokenRequest);

}
