package org.jfantasy.framework.security;

/**
 * SpringSecurityUtils
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 13:27
 */
public class SpringSecurityUtils {

    private SpringSecurityUtils() {
    }

    public static <T extends LoginUser> T getCurrentUser(Class<T> clazz) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication().isAuthenticated()) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof LoginUser) {
                return clazz.cast(principal);
            }
        }
        return null;
    }

    public static LoginUser getCurrentUser() {
        return getCurrentUser(LoginUser.class);
    }

}
