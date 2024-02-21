package net.asany.jfantasy.framework.security.authorization.policy.context;

import net.asany.jfantasy.framework.security.authentication.Authentication;

public interface RequestContextBuilder {

  /**
   * 是否支持
   *
   * @param authentication 认证信息
   * @return 是否支持
   */
  boolean supports(Authentication authentication);

  RequestContext build(Authentication authentication);
}
