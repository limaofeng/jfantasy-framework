package net.asany.jfantasy.framework.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.AuthorityUtils;

/**
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
    this.authorities = Collections.unmodifiableList(new ArrayList<>(authorities));
  }
}
