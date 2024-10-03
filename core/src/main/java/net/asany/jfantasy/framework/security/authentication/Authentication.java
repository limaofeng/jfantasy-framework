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

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 身份验证
 *
 * @author limaofeng
 */
public interface Authentication extends Principal, Serializable {

  /**
   * 权限
   *
   * @return Collection<GrantedAuthority>
   */
  Collection<GrantedAuthority> getAuthorities();

  /**
   * 凭证 密码 / Token 等
   *
   * @return 凭证
   */
  <C> C getCredentials();

  /**
   * 详情
   *
   * @return 认证详情
   */
  <O extends AuthenticationDetails> O getDetails();

  /**
   * 当事人
   *
   * @return 用户
   */
  <P> P getPrincipal();

  /**
   * 是否授权 （登录）
   *
   * @return boolean
   */
  boolean isAuthenticated();

  /**
   * 设置
   *
   * @param isAuthenticated boolean
   */
  void setAuthenticated(boolean isAuthenticated);
}
