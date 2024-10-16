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

import lombok.*;
import net.asany.jfantasy.framework.dao.Tenantable;
import net.asany.jfantasy.framework.security.core.AbstractAuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;

/**
 * 登录用户对象
 *
 * @author limaofeng
 * @version V1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoginUser extends AbstractAuthenticatedPrincipal
    implements UserDetails, AuthenticatedPrincipal, Tenantable {
  /** 用户名 */
  private String username;

  /** 密码 */
  private String password;

  /** 用户ID */
  private Long id;

  /** 用户类型 */
  private String type;

  /** 名称 */
  private String name;

  /** 称号 */
  private String title;

  /** 头像 */
  private String avatar;

  /** 邮箱 */
  private String email;

  /** 签名 */
  private String signature;

  /** 组名 */
  private String group;

  /** 电话 */
  private String phone;

  /** 启用状态 */
  @Builder.Default private boolean enabled = true;

  /** 账户过期状态 */
  @Builder.Default private boolean accountNonExpired = true;

  /** 账户锁定状态 */
  @Builder.Default private boolean accountNonLocked = true;

  /** 凭证过期状态 */
  @Builder.Default private boolean credentialsNonExpired = true;

  /** 租户ID */
  private String tenantId;
}
