package net.asany.jfantasy.framework.security.core.authority;

import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 权限构建器
 *
 * <p>用于构建权限
 */
public interface AuthorityBuilder {
  GrantedAuthority buildAuthority(String authority);
}
