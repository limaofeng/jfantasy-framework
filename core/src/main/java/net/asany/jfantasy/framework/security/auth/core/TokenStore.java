package net.asany.jfantasy.framework.security.auth.core;

import java.util.Collection;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;

/**
 * ToKen 存储器
 *
 * @param <T>
 */
public interface TokenStore<T extends AuthToken> {

  /**
   * 通过 Token 获取身份
   *
   * @param token 令牌
   * @return AuthenticationToken
   */
  Authentication readAuthentication(AuthenticationToken<String> token);

  /**
   * 通过 Token 获取身份
   *
   * @param token 令牌
   * @return BearerTokenAuthentication
   */
  AuthenticationToken<T> readAuthentication(String token);

  /**
   * 存储 Token 对应的获取身份
   *
   * @param token 令牌
   * @param authentication 身份验证
   */
  void storeAccessToken(T token, Authentication authentication);

  /**
   * 获取 AccessToken 信息
   *
   * @param tokenValue 令牌值
   * @return AuthToken
   */
  T readAccessToken(String tokenValue);

  /**
   * 移除 AccessToken 信息
   *
   * @param token 令牌
   */
  void removeAccessToken(T token);

  /**
   * 存储 refreshToken 对应的获取身份
   *
   * @param refreshToken 刷新令牌
   * @param authentication 身份验证
   */
  void storeRefreshToken(AuthRefreshToken refreshToken, Authentication authentication);

  /**
   * 获取 readRefreshToken 信息
   *
   * @param tokenValue 令牌值
   * @return OAuth2RefreshToken
   */
  AuthRefreshToken readRefreshToken(String tokenValue);

  /**
   * 通过 RefreshToken 获取身份信息
   *
   * @param token 令牌
   * @return AuthenticationToken
   */
  AuthenticationToken readAuthenticationForRefreshToken(AuthRefreshToken token);

  /**
   * 移除 refreshToken
   *
   * @param token 令牌
   */
  void removeRefreshToken(AuthRefreshToken token);

  /**
   * 通过 refreshToken 移除 AccessToken
   *
   * @param refreshToken 刷新令牌
   */
  void removeAccessTokenUsingRefreshToken(AuthRefreshToken refreshToken);

  /**
   * 获取 OAuth2AccessToken
   *
   * @param authentication 授权
   * @return AuthToken
   */
  T getAccessToken(AuthenticationToken<String> authentication);

  /**
   * 获取 clientId 的全部 Token
   *
   * @param clientId 客户端ID
   * @param userName 用户名
   * @return Collection<OAuth2AccessToken>
   */
  Collection<AuthToken> findTokensByClientIdAndUserName(String clientId, String userName);

  /**
   * 获取 clientId 的全部 Token
   *
   * @param clientId 客户端ID
   * @return Collection<OAuth2AccessToken>
   */
  Collection<AuthToken> findTokensByClientId(String clientId);
}
