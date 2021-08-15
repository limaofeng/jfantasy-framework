package org.jfantasy.framework.security.core;

/**
 * 身份验证的主体
 *
 * @author limaofeng
 */
public interface AuthenticatedPrincipal {

  /**
   * 当事人名称
   *
   * @return
   */
  String getName();
}
