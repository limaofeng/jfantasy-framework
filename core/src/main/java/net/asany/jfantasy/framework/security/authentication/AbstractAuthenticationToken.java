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

import java.util.Collection;
import java.util.List;
import lombok.Data;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.AuthorityUtils;

/**
 * 抽象身份验证令牌
 *
 * <p>该类实现了 Authentication 接口，提供了 Authentication 接口的基本实现。
 *
 * @author limaofeng
 */
@Data
public abstract class AbstractAuthenticationToken<T> implements AuthenticationToken<T> {
  protected String name;
  protected AuthenticationDetails details;
  protected Collection<GrantedAuthority> authorities;
  protected boolean authenticated;

  public AbstractAuthenticationToken(Collection<GrantedAuthority> authorities) {
    if (authorities == null) {
      this.authorities = AuthorityUtils.NO_AUTHORITIES;
      return;
    }
    this.authorities = List.copyOf(authorities);
  }
}
