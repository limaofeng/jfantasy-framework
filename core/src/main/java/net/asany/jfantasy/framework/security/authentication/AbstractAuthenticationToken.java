package net.asany.jfantasy.framework.security.authentication;

import lombok.Data;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.List;

/**
 * 抽象身份验证令牌
 * <p>
 * 该类实现了 Authentication 接口，提供了 Authentication 接口的基本实现。
 *
 * @author limaofeng
 */
@Data
public abstract class AbstractAuthenticationToken implements Authentication {
  private String name;
  private Object details;
  private Collection<GrantedAuthority> authorities;
  private boolean authenticated;

  public AbstractAuthenticationToken(Collection<GrantedAuthority> authorities) {
    if (authorities == null) {
      this.authorities = AuthorityUtils.NO_AUTHORITIES;
      return;
    }
    this.authorities = List.copyOf(authorities);
  }
}
