package net.asany.jfantasy.framework.security.auth.core.token;

import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;

public interface ResourceServerTokenServices<T extends AuthToken> {

  BearerTokenAuthentication loadAuthentication(BearerTokenAuthenticationToken token);

  BearerTokenAuthentication loadAuthentication(String accessToken);

  T readAccessToken(BearerTokenAuthenticationToken accessToken);

  T readAccessToken(String accessToken);
}
