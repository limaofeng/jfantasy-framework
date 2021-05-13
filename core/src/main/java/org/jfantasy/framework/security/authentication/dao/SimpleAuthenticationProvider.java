package org.jfantasy.framework.security.authentication.dao;

import org.jfantasy.framework.security.authentication.*;
import org.jfantasy.framework.security.core.userdetails.SimpleUserDetailsService;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author limaofeng
 */
public class SimpleAuthenticationProvider extends AbstractSimpleUserDetailsAuthenticationProvider {

    private SimpleUserDetailsService userDetailsService;

    private Class<?> authenticationClass;

    public SimpleAuthenticationProvider(Class authentication) {
        this.authenticationClass = authentication;
    }

    @Override
    public boolean supports(Class authentication) {
        return (this.authenticationClass.isAssignableFrom(authentication));
    }

    @Override
    public UserDetails retrieveUser(SimpleAuthenticationToken authentication) {
        Object token = determineToken(authentication);
        try {
            UserDetails loadedUser = this.userDetailsService.loadUserByToken(token);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (InternalAuthenticationServiceException | UsernameNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private Object determineToken(Authentication authentication) {
        return authentication.getCredentials();
    }

    public void setUserDetailsService(SimpleUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
