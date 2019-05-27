package org.jfantasy.framework.security;

import org.springframework.boot.actuate.endpoint.SecurityContext;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-02 13:27
 */
public class SpringSecurityUtils {

    private SpringSecurityUtils() {
    }

    public static <T extends LoginUser> T getCurrentUser(Class<T> clazz) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Object principal = context.getPrincipal();
            if (principal instanceof LoginUser) {
                return clazz.cast(principal);
            }
        }
        return null;
    }

    public static LoginUser getCurrentUser() {
        return getCurrentUser(LoginUser.class);
    }

    public static boolean isUserInRole(String name) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return false;
        }
        return context.isUserInRole(name);
    }

}
