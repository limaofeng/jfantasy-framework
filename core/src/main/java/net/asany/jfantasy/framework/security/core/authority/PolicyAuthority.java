package net.asany.jfantasy.framework.security.core.authority;

import net.asany.jfantasy.framework.security.core.GrantedAuthority;

public class PolicyAuthority implements GrantedAuthority {

  public static final String TYPE = "POLICY";
  private final String policy;

  public PolicyAuthority(String policy) {
    this.policy = policy;
  }

  @Override
  public String getAuthority() {
    return TYPE + "_" + policy;
  }

  public String getPolicy() {
    return this.policy;
  }
}
