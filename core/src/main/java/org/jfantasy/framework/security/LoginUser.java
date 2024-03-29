package org.jfantasy.framework.security;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jfantasy.framework.dao.Tenantable;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.core.user.OAuth2User;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * 登录用户对象
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-08 17:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails, Principal, OAuth2User, Tenantable {
  /** 用户名 */
  private String username;
  /** 密码 */
  private String password;
  /** 用户ID */
  private Long uid;
  /** 用户类型 */
  private String type;
  /** 名称 */
  private String name;
  /** 称号 */
  private String title;
  /** 头像 */
  private String avatar;
  /** 邮箱 */
  private String email;
  /** 签名 */
  private String signature;
  /** 组名 */
  private String group;
  /** 电话 */
  private String phone;
  /** 扩展属性 */
  @JsonIgnore private Map<String, Object> data;
  /** 启用状态 */
  @Builder.Default private boolean enabled = true;
  /** 账户过期状态 */
  @Builder.Default private boolean accountNonExpired = true;
  /** 账户锁定状态 */
  @Builder.Default private boolean accountNonLocked = true;
  /** 凭证过期状态 */
  @Builder.Default private boolean credentialsNonExpired = true;
  /** 权限 */
  @JsonSerialize(using = GrantedAuthority.GrantedAuthoritiesSerializer.class)
  @JsonDeserialize(using = GrantedAuthority.GrantedAuthoritiesDeserializer.class)
  private Set<GrantedAuthority> authorities;
  /** 租户ID */
  private String tenantId;

  @JsonAnySetter
  public void setAttribute(String key, Object value) {
    if (this.data == null) {
      this.data = new HashMap<>(0);
    }
    this.data.put(key, value);
  }

  @Override
  public <A> A getAttribute(String name) {
    if (this.data == null) {
      this.data = new HashMap<>();
    }
    return (A) this.data.get(name);
  }

  @Override
  @JsonAnyGetter
  public Map<String, Object> getAttributes() {
    return ObjectUtil.defaultValue(this.data, Collections.emptyMap());
  }

  public void setAuthorities(Set<GrantedAuthority> authorities) {
    this.authorities = authorities;
  }
}
