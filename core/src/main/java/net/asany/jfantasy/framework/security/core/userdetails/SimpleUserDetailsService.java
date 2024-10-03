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

/**
 * 基于 Token 获取用户信息
 *
 * @author limaofeng
 */
public interface SimpleUserDetailsService<T> {

  /**
   * 获取用户
   *
   * @param token 凭证
   * @return 返回用户
   * @throws UsernameNotFoundException 未查找用户
   */
  UserDetails loadUserByToken(T token) throws UsernameNotFoundException;
}
