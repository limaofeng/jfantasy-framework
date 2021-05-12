package org.jfantasy.framework.security.core.userdetails;

public interface SimpleUserDetailsService {
    UserDetails loadUserByToken(String token) throws UsernameNotFoundException;
}
