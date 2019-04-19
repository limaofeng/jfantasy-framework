package org.jfantasy.framework.security;

import org.springframework.boot.actuate.endpoint.SecurityContext;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-02 13:27
 */
public class SecurityContextHolder {

    private static ThreadLocal<SecurityContext> holder = new ThreadLocal<>();

    public static SecurityContext getContext() {
        SecurityContext securityContextHolder = holder.get();
        if (securityContextHolder == null) {
            throw new SecurityException("未初始化 SecurityContext ");
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

}
