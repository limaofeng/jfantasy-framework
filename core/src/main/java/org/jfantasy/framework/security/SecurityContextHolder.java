package org.jfantasy.framework.security;

/**
 * 安全上下文持有人
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 13:27
 */
public class SecurityContextHolder {

  private static ThreadLocal<SecurityContext> holder = new ThreadLocal<>();

  public static SecurityContext getContext() {
    SecurityContext securityContextHolder = holder.get();
    if (securityContextHolder == null) {
      return null;
    }
    return holder.get();
  }

  public static void setContext(SecurityContext context) {
    SecurityContext securityContextHolder = holder.get();
    if (securityContextHolder != null) {
      holder.remove();
    }
    holder.set(context);
  }

  public static void clear() {
    SecurityContext securityContextHolder = holder.get();
    if (securityContextHolder != null) {
      holder.remove();
    }
  }

  public static SecurityContext createEmptyContext() {
    return new SecurityContext();
  }

  public static void clearContext() {
    clear();
  }
}
