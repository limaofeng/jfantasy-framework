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
package net.asany.jfantasy.framework.security;

import java.util.Optional;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.util.common.ClassUtil;

/**
 * 用户字段扩展变量接口
 *
 * <p>但与用户关联的变量，但由其他模块实现，比如： 用户的网盘，用户的邮件设置等等
 */
public interface PrincipalDynamicAttribute<P extends AuthenticatedPrincipal, T> {

  default boolean supports(Class<? extends AuthenticatedPrincipal> authentication) {
    return ClassUtil.getInterfaceGenricType(this.getClass(), PrincipalDynamicAttribute.class)
        .isAssignableFrom(authentication);
  }

  /**
   * 获取字段名称
   *
   * @return 字段名称
   */
  String name();

  /**
   * 根据 LoginUser 获取字段的值
   *
   * @param user 用户对象
   * @return 字段值
   */
  Optional<T> value(P user);
}
