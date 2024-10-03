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
package net.asany.jfantasy.framework.security.crypto.password;

/**
 * 密码编码器
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-28 15:00
 */
public interface PasswordEncoder {

  /**
   * 加密逻辑
   *
   * @param rawPassword 原密码
   * @return 加密后的密码
   */
  String encode(CharSequence rawPassword);

  /**
   * 密码比较方法
   *
   * @param rawPassword 未加密密码 一般指用户输入的原始密码
   * @param encodedPassword 已加密密码 数据库中存储的已加密密码
   * @return 是否相同
   */
  boolean matches(CharSequence rawPassword, String encodedPassword);
}
