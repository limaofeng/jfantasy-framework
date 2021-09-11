package org.jfantasy.framework.security.core;

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

  @Override
  public String getAuthority() {
    return type + "_" + code;
  }
}
