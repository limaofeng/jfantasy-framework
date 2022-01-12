package org.jfantasy.framework.security.oauth2.core;

import java.util.Collection;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;

/**
 * ToKen 存储器
 *
 * @author limaofeng
 */
public interface TokenStore {

  /**
   * 通过 Token 获取身份
   *
   * @param token 令牌
   * @return BearerTokenAuthentication
   */
  BearerTokenAuthentication readAuthentication(BearerTokenAuthenticationToken token);

  /**
   * 通过 Token 获取身份
   *
   * @param token 令牌
   * @return BearerTokenAuthentication
   */
  BearerTokenAuthentication readAuthentication(String token);

  /**
   * 存储 Token 对应的获取身份
   *
   * @param token 令牌
   * @param authentication 身份验证
   */
  void storeAccessToken(OAuth2AccessToken token, Authentication authentication);

  /**
   * 获取 AccessToken 信息
   *
   * @param tokenValue 令牌值
   * @return OAuth2AccessToken
   */
  OAuth2AccessToken readAccessToken(String tokenValue);

  /**
   * 移除 AccessToken 信息
   *
   * @param token 令牌
   */
  void removeAccessToken(OAuth2AccessToken token);

  /**
   * 存储 refreshToken 对应的获取身份
   *
   * @param refreshToken 刷新令牌
   * @param authentication 身份验证
   */
  void storeRefreshToken(OAuth2RefreshToken refreshToken, Authentication authentication);

  /**
   * 获取 readRefreshToken 信息
   *
   * @param tokenValue 令牌值
   * @return OAuth2RefreshToken
   */
  OAuth2RefreshToken readRefreshToken(String tokenValue);

  /**
   * 通过 RefreshToken 获取身份信息
   *
   * @param token 令牌
   * @return BearerTokenAuthentication
   */
  BearerTokenAuthentication readAuthenticationForRefreshToken(OAuth2RefreshToken token);

  /**
   * 移除 refreshToken
   *
   * @param token 令牌
   */
  void removeRefreshToken(OAuth2RefreshToken token);

  /**
   * 通过 refreshToken 移除 AccessToken
   *
   * @param refreshToken 刷新令牌
   */
  void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken);

  /**
   * 获取 OAuth2AccessToken
   *
   * @param authentication 授权
   * @return OAuth2AccessToken
   */
  OAuth2AccessToken getAccessToken(BearerTokenAuthentication authentication);

  /**
   * 获取 clientId 的全部 Token
   *
   * @param clientId 客户端ID
   * @param userName 用户名
   * @return Collection<OAuth2AccessToken>
   */
  Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName);

  /**
   * 获取 clientId 的全部 Token
   *
   * @param clientId 客户端ID
   * @return Collection<OAuth2AccessToken>
   */
  Collection<OAuth2AccessToken> findTokensByClientId(String clientId);
}
