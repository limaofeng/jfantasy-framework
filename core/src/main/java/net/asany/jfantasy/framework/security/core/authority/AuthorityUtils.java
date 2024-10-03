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
package net.asany.jfantasy.framework.security.core.authority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.builders.PolicyAuthorityBuilder;
import net.asany.jfantasy.framework.security.core.authority.builders.RoleAuthorityBuilder;
import net.asany.jfantasy.framework.security.core.authority.builders.SimpleAuthorityBuilder;

/**
 * 权限工具类
 *
 * <p>用于创建权限
 *
 * @author limaofeng
 */
public class AuthorityUtils {

  public static final List<GrantedAuthority> NO_AUTHORITIES = Collections.emptyList();

  private static GrantedAuthorityFactory grantedAuthorityFactory;

  public static GrantedAuthorityFactory getFactory() {
    if (grantedAuthorityFactory == null) {
      grantedAuthorityFactory = new GrantedAuthorityFactory();
      grantedAuthorityFactory.registerBuilder(PolicyAuthority.TYPE, new PolicyAuthorityBuilder());
      grantedAuthorityFactory.registerBuilder(RoleAuthority.TYPE, new RoleAuthorityBuilder());
      grantedAuthorityFactory.setDefaultAuthorityBuilder(new SimpleAuthorityBuilder());
    }
    return grantedAuthorityFactory;
  }

  public static List<GrantedAuthority> createAuthorityList(String... authorities) {
    return Arrays.stream(authorities)
        .map(authority -> getFactory().createAuthority(authority))
        .collect(Collectors.toList());
  }
}
