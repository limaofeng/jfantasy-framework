package net.asany.jfantasy.framework.security.core.authority.builders;

import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.AuthorityBuilder;
import net.asany.jfantasy.framework.security.core.authority.RoleAuthority;

public class RoleAuthorityBuilder implements AuthorityBuilder {

  @Override
  public GrantedAuthority buildAuthority(String authority) {
    return new RoleAuthority(authority.substring(authority.indexOf("_") + 1));
  }
}
