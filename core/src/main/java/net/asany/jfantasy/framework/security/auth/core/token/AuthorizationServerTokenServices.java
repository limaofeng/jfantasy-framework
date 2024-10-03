/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.security.auth.core.token;

import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;

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
  T createAccessToken(Authentication authentication);

  /**
   * 获取访问令牌
   *
   * @param authentication 授权信息
   * @return 访问令牌
   */
  T getAccessToken(Authentication authentication);

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
