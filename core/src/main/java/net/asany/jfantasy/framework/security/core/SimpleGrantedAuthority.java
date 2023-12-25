package net.asany.jfantasy.framework.security.core;

import lombok.*;

/**
 * 简单的授权
 *
 * @author limaofeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleGrantedAuthority implements GrantedAuthority {
  private String type;
  private String code;

  public SimpleGrantedAuthority(String authority) {
    int index = authority.indexOf("_");
    type = authority.substring(0, index);
    code = authority.substring(index + 1);
  }

  public static SimpleGrantedAuthority newInstance(String authority) {
    return new SimpleGrantedAuthority(authority);
  }

  @Override
  public String getAuthority() {
    return type + "_" + code;
  }

  @Override
  public String toString() {
    return getAuthority();
  }
}
