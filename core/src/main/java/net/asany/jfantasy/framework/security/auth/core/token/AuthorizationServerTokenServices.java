package net.asany.jfantasy.framework.security.auth.core.token;

import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;

/**
 * 授权服务器令牌服务
 *
 * <p>用于创建和获取令牌
 *
 * @author limaofeng
 */
public interface AuthorizationServerTokenServices<T extends AuthToken> {

  /**
   * 创建访问令牌
   *
   * @param authentication 授权信息
   * @return 访问令牌
   */
  T createAccessToken(AuthenticationToken authentication);

  /**
   * 获取访问令牌
   *
   * @param authentication 授权信息
   * @return 访问令牌
   */
  T getAccessToken(AuthenticationToken authentication);

  /**
   * 刷新访问令牌
   *
   * @param refreshToken 刷新令牌
   * @param tokenRequest 令牌请求
   * @return 访问令牌
   */
  default AuthToken refreshAccessToken(String refreshToken, String tokenRequest) {
    return null;
  }
}
