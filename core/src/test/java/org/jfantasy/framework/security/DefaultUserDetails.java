package org.jfantasy.framework.security;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.core.userdetails.UserDetails;

@Data
@Builder
public class DefaultUserDetails implements UserDetails {
  private String username;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;
  @Builder.Default private boolean enabled = true;
  @Builder.Default private boolean accountNonExpired = true;
  @Builder.Default private boolean accountNonLocked = true;
  @Builder.Default private boolean credentialsNonExpired = true;
}
