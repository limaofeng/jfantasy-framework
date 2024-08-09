package net.asany.jfantasy.framework.security;

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
import lombok.*;
import net.asany.jfantasy.framework.dao.Tenantable;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.UserAttributeService;
import net.asany.jfantasy.framework.security.core.user.OAuth2User;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.util.common.ObjectUtil;

/**
 * 登录用户对象
 *
 * @author limaofeng
 * @version V1.0
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
  @Setter
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
    UserAttributeService userAttributeService =
        SpringBeanUtils.getBeanByType(UserAttributeService.class);
    if (userAttributeService.hasAttribute(name)) {
      return userAttributeService.getAttributeValue(this, name);
    }
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
}
