package net.asany.jfantasy.framework.security.oauth2.core.token;

import net.asany.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.oauth2.core.OAuth2Authentication;

/**
 * @author limaofeng
 */
public interface AuthorizationServerTokenServices {

  OAuth2AccessToken createAccessToken(OAuth2Authentication authentication);

  OAuth2AccessToken getAccessToken(OAuth2Authentication authentication);

  // OAuth2AccessToken refreshAccessToken(String refreshToken, TokenRequest
  // tokenRequest);

}
