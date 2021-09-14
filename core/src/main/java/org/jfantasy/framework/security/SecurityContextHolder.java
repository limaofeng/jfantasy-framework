package org.jfantasy.framework.security;

/**
 * 安全上下文持有人
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 13:27
 */
public class SecurityContextHolder {

  private static final ThreadLocal<SecurityContext> HOLDER = new ThreadLocal<>();

  public static SecurityContext getContext() {
    return HOLDER.get();
  }

  public static void setContext(SecurityContext context) {
    SecurityContext securityContextHolder = HOLDER.get();
    if (securityContextHolder != null) {
      HOLDER.remove();
    }
    HOLDER.set(context);
  }

  public static void clear() {
    SecurityContext securityContextHolder = HOLDER.get();
    if (securityContextHolder != null) {
      HOLDER.remove();
    }
  }

  public static SecurityContext createEmptyContext() {
    return new SecurityContext();
  }
}
