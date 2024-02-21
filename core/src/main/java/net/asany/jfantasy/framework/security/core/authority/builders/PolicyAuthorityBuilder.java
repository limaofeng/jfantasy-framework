package net.asany.jfantasy.framework.security.core.authority.builders;

import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.AuthorityBuilder;
import net.asany.jfantasy.framework.security.core.authority.PolicyAuthority;

public class PolicyAuthorityBuilder implements AuthorityBuilder {

  @Override
  public GrantedAuthority buildAuthority(String authority) {
    return new PolicyAuthority(authority.substring(authority.indexOf("_") + 1));
  }
}
