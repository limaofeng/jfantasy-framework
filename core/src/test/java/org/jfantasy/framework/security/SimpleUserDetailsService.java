package org.jfantasy.framework.security;

import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;

public class SimpleUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return DefaultUserDetails.builder().username("limaofeng").password("123456789").build();
    }
}
