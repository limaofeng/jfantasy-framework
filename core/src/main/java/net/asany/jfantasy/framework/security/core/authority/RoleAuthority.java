package net.asany.jfantasy.framework.security.core.authority;

import lombok.Getter;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/** 角色 */
@Getter
public class RoleAuthority implements GrantedAuthority {

  public static final String TYPE = "ROLE";
  private final String role;

  public RoleAuthority(String role) {
    this.role = role;
  }

  @Override
  public String getAuthority() {
    return TYPE + "_" + role;
  }
}
