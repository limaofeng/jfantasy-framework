package org.jfantasy.framework.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.core.authority.AuthorityUtils;

/**
 * @author limaofeng
 */
@Data
public abstract class AbstractAuthenticationToken implements Authentication {
  private String name;
  private Object details;
  private Collection<? extends GrantedAuthority> authorities;
  private boolean authenticated;

  public AbstractAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
    if (authorities == null) {
      this.authorities = AuthorityUtils.NO_AUTHORITIES;
      return;
    }
    this.authorities = Collections.unmodifiableList(new ArrayList<>(authorities));
  }
}
