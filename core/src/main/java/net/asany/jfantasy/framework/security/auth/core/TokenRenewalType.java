package net.asany.jfantasy.framework.security.auth.core;

public enum TokenRenewalType {
  /** 不允许 */
  NOT_ALLOW,
  /** 回话形式 */
  SESSION,
  /** 通过 refresh_token 实现 */
  REFRESH_TOKEN
}
