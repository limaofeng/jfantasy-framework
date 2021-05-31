package org.jfantasy.framework.security.oauth2.provider.token;

import org.jfantasy.framework.security.oauth2.provider.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;

public interface ResourceServerTokenServices {

    OAuth2Authentication loadAuthentication(String accessToken);

    OAuth2AccessToken readAccessToken(String accessToken);

}
