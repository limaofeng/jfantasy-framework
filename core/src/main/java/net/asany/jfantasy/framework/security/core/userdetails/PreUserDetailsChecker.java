package net.asany.jfantasy.framework.security.core.userdetails;

/**
 * 前置验证 可以通过 @Order 注解跳转多个验证器的顺序
 *
 * @author limaofeng
 */
public interface PreUserDetailsChecker extends UserDetailsChecker {}
