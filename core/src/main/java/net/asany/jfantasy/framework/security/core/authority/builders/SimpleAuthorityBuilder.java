package net.asany.jfantasy.framework.security.core.authority.builders;

import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.AuthorityBuilder;
import net.asany.jfantasy.framework.security.core.authority.SimpleGrantedAuthority;

public class SimpleAuthorityBuilder implements AuthorityBuilder {

  @Override
  public GrantedAuthority buildAuthority(String authority) {
    return SimpleGrantedAuthority.newInstance(authority);
  }
}
