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

import java.util.concurrent.CompletableFuture;

/**
 * 用户服务
 *
 * @author limaofeng
 */
public interface UserDetailsService<T extends UserDetails> {

  /**
   * 通过用户名查询用户
   *
   * @param tenantId 租户ID
   * @param username 用户名
   * @return 用户
   * @throws UsernameNotFoundException 未查询到用户
   */
  T loadUserByUsername(String tenantId, String username) throws UsernameNotFoundException;

  /**
   * 根据用户ID加载用户信息
   *
   * @param id 用户ID
   * @return 用户信息
   */
  default CompletableFuture<T> loadUserById(Long id) {
    throw new UnsupportedOperationException();
  }

  /**
   * 根据手机号码加载用户信息
   *
   * @param tenantId 租户ID
   * @param phone 手机号码
   * @return 用户信息
   */
  default T loadUserByPhone(String tenantId, String phone) {
    throw new UnsupportedOperationException();
  }
}
