package org.jfantasy.framework.security;

import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;

public class SimpleUserDetailsService implements UserDetailsService<UserDetails> {
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return DefaultUserDetails.builder().username("limaofeng").password("123456789").build();
  }
}
