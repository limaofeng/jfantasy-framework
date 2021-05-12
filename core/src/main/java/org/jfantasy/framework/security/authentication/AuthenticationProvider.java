package org.jfantasy.framework.security.authentication;

import org.jfantasy.framework.security.AuthenticationException;

public interface AuthenticationProvider<T extends Authentication> {

    boolean	supports(Class<?> authentication);

    Authentication authenticate(T authentication) throws AuthenticationException;

}
