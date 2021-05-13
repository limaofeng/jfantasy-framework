package org.jfantasy.framework.security.authentication.dao;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.*;
import org.jfantasy.framework.security.core.userdetails.DefaultAuthenticationChecks;
import org.jfantasy.framework.security.core.userdetails.UserDetailsChecker;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.security.core.userdetails.UserDetails;

/**
 * @author limaofeng
 */
@Slf4j
public abstract class AbstractUserDetailsAuthenticationProvider implements AuthenticationProvider<UsernamePasswordAuthenticationToken> {

    private boolean hideUserNotFoundExceptions;

    private UserDetailsChecker preAuthenticationChecks = new DefaultAuthenticationChecks(new DefaultPreAuthenticationChecks());
    private UserDetailsChecker postAuthenticationChecks = new DefaultAuthenticationChecks(new DefaultPostAuthenticationChecks());

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    /**
     * 查找用户
     *
     * @param username 用户名
     * @param token    密码
     * @return 返回用户
     */
    protected abstract UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken token);

    private String determineUsername(Authentication authentication) {
        return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
    }

    @Override
    public Authentication authenticate(UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String username = determineUsername(authentication);

        UserDetails user;
        try {
            user = this.retrieveUser(username, authentication);
        } catch (UsernameNotFoundException ex) {
            log.debug("Failed to find user '" + username + "'");
            if (!this.hideUserNotFoundExceptions) {
                throw ex;
            }
            throw new BadCredentialsException("Bad credentials");
        }
        this.preAuthenticationChecks.check(user);
        additionalAuthenticationChecks(user, authentication);
        this.postAuthenticationChecks.check(user);
        return createSuccessAuthentication(user, authentication, user);
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials(), user.getAuthorities());
        result.setDetails(authentication.getDetails());
        log.debug("Authenticated user");
        return result;
    }

    protected abstract void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException;

    public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public static class DefaultPreAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                throw new LockedException("User account is locked");
            }
            if (!user.isEnabled()) {
                throw new DisabledException("User is disabled");
            }
            if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException("User account has expired");
            }
        }

    }

    public static class DefaultPostAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                throw new CredentialsExpiredException("User credentials have expired");
            }
        }

    }

    public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

}
