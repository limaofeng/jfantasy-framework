package net.asany.jfantasy.framework.security;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;

@Data
@Builder
public class DefaultUserDetails implements UserDetails {
  private String username;
  private String password;
  private Collection<GrantedAuthority> authorities;
  @Builder.Default private boolean enabled = true;
  @Builder.Default private boolean accountNonExpired = true;
  @Builder.Default private boolean accountNonLocked = true;
  @Builder.Default private boolean credentialsNonExpired = true;
}
