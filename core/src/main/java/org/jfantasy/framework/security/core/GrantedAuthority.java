package org.jfantasy.framework.security.core;

import org.jfantasy.framework.jackson.deserializer.StringToSetDeserializer;
import org.jfantasy.framework.jackson.serializer.SetToStringSerializer;

/**
 * 授予的权限
 *
 * @author limaofeng
 */
public interface GrantedAuthority {
  /**
   * 权限编码
   *
   * @return Authority
   */
  String getAuthority();

  class GrantedAuthoritiesSerializer extends SetToStringSerializer<GrantedAuthority> {
    @Override
    public String itemSerialize(GrantedAuthority authority) {
      return authority.getAuthority();
    }
  }

  class GrantedAuthoritiesDeserializer extends StringToSetDeserializer<GrantedAuthority> {
    @Override
    public GrantedAuthority itemDeserialize(String text) {
      return new SimpleGrantedAuthority(text);
    }
  }
}
