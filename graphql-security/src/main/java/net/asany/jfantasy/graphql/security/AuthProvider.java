package net.asany.jfantasy.graphql.security;

/**
 * 认证提供者
 * <p>
 * 用于标识认证方式
 */
public enum AuthProvider {
  /**
   * API Key 认证
   * <p>
   * 通过在请求头中添加 `X-API-KEY` 来进行认证
   */
  apiKey,
  /**
   * IAM 认证
   * <p>
   * 通过在请求头中添加 `Authorization: Bearer {token}` 来进行认证
   */
  iam,
  /**
   * OIDC 认证
   * <p>
   * 通过在请求头中添加 `Authorization: Bearer {token}` 来进行认证
   */
  oidc,
  /**
   * 用户池认证
   * <p>
   * 通过在请求头中添加 `Authorization: Bearer {token}` 来进行认证
   */
  userPools
}
