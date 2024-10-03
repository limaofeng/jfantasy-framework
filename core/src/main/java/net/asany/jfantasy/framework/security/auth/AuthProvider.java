/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.security.auth;

/**
 * 认证提供者
 *
 * <p>用于标识认证方式
 */
public enum AuthProvider {
  /**
   * API Key 认证
   *
   * <p>通过在请求头中添加 `X-API-KEY` 来进行认证
   */
  apiKey,
  /**
   * IAM 认证
   *
   * <p>通过在请求头中添加 `Authorization: Bearer {token}` 来进行认证
   */
  iam,
  /**
   * OIDC 认证
   *
   * <p>通过在请求头中添加 `Authorization: Bearer {token}` 来进行认证
   */
  oidc,
  /**
   * 用户池认证
   *
   * <p>通过在请求头中添加 `Authorization: Bearer {token}` 来进行认证
   */
  userPools
}
