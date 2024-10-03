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
package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.AuthenticationException;

/**
 * 身份验证程序
 *
 * @param <T>
 * @author limaofeng
 */
public interface AuthenticationProvider<T extends Authentication> {

  /**
   * 判断是否支持此身份认证逻辑
   *
   * @param authentication 身份验证
   * @return Boolean
   */
  boolean supports(Class<? extends Authentication> authentication);

  /**
   * 进行身份验证
   *
   * @param authentication 身份验证
   * @return Boolean
   * @throws AuthenticationException 异常
   */
  Authentication authenticate(T authentication) throws AuthenticationException;
}
