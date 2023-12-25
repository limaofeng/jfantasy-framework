package net.asany.jfantasy.framework.security.oauth2.core.token;

import net.asany.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;

public interface ResourceServerTokenServices {

  BearerTokenAuthentication loadAuthentication(BearerTokenAuthenticationToken token);

  BearerTokenAuthentication loadAuthentication(String accessToken);

  OAuth2AccessToken readAccessToken(BearerTokenAuthenticationToken accessToken);

  OAuth2AccessToken readAccessToken(String accessToken);
}
