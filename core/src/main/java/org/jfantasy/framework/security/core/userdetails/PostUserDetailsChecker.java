package org.jfantasy.framework.security.core.userdetails;

/**
 * 后置验证 可以通过 @Order 注解跳转多个验证器的顺序
 *
 * @author limaofeng
 */
public interface PostUserDetailsChecker extends UserDetailsChecker {}
