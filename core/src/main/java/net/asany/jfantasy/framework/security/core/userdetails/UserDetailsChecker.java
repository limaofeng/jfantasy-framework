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
package net.asany.jfantasy.framework.security.core.userdetails;

import net.asany.jfantasy.framework.security.auth.AuthenticationToken;

/**
 * 用户详情检测器
 *
 * @author limaofeng
 */
public interface UserDetailsChecker {

  /**
   * 判断是否需要进行认证检查
   *
   * @param authenticationToken 认证令牌
   * @return 是否需要进行认证检查
   */
  default boolean needsCheck(AuthenticationToken<?> authenticationToken) {
    return true;
  }

  /**
   * 检查方法
   *
   * @param user 用户
   * @param authenticationToken 认证令牌
   */
  void check(UserDetails user, AuthenticationToken<?> authenticationToken);
}
