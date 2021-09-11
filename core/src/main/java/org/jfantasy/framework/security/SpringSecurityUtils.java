package org.jfantasy.framework.security;

/**
 * SpringSecurityUtils
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 13:27
 */
public class SpringSecurityUtils {

  private SpringSecurityUtils() {}

  public SecurityContext getContext() {
    return SecurityContextHolder.getContext();
  }

  public static <T extends LoginUser> T getCurrentUser(Class<T> clazz) {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null || !context.isAuthenticated()) {
      return null;
    }
    return context.getPrincipal(clazz);
  }

  public static LoginUser getCurrentUser() {
    return getCurrentUser(LoginUser.class);
  }
}
