package org.jfantasy.framework.security.oauth2.core.token;

import org.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;

public interface ResourceServerTokenServices {

  BearerTokenAuthentication loadAuthentication(String accessToken);

  OAuth2AccessToken readAccessToken(String accessToken);
}
