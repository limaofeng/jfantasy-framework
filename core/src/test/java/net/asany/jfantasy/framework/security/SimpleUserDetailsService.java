package net.asany.jfantasy.framework.security;

import net.asany.jfantasy.framework.security.core.userdetails.UserDetails;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetailsService;
import net.asany.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;

public class SimpleUserDetailsService implements UserDetailsService<UserDetails> {
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return DefaultUserDetails.builder().username("limaofeng").password("123456789").build();
  }
}
