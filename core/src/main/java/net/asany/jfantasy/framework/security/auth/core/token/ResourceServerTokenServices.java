package net.asany.jfantasy.framework.security.auth.core.token;

import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;

/**
 * 贤源服务器令牌服务
 *
 * @param <T>
 */
public interface ResourceServerTokenServices<T extends AuthToken> {

  AuthenticationToken<T> loadAuthentication(AuthenticationToken<String> authenticationToken);

  AuthenticationToken<T> loadAuthentication(String token);

  T readAccessToken(AuthenticationToken<String> authenticationToken);

  T readAccessToken(String accessToken);
}
