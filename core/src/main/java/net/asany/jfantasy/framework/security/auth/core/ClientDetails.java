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
package net.asany.jfantasy.framework.security.auth.core;

import java.util.*;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 客户端信息
 *
 * @author limaofeng
 */
public interface ClientDetails extends AuthenticatedPrincipal {

  Long getId();

  /**
   * 附加信息
   *
   * @return Map
   */
  Map<String, Object> getAdditionalInformation();

  /**
   * 附加信息
   *
   * @param key 键
   * @param clazz 值类型
   * @return T
   * @param <T> Object
   */
  <T> T getAdditionalInformation(String key, Class<T> clazz);

  /**
   * 权限
   *
   * @return Collection<GrantedAuthority>
   */
  Collection<GrantedAuthority> getAuthorities();

  /**
   * 支持的授权方式
   *
   * @return Set<String>
   */
  Set<String> getAuthorizedGrantTypes();

  /**
   * 客户端 ID
   *
   * @return String
   */
  String getClientId();

  /**
   * 客户端密钥（多个）
   *
   * @param type 密钥类型
   * @return Set<ClientSecret>
   */
  Set<ClientSecret> getClientSecrets(ClientSecretType type);

  /**
   * 客户端密钥
   *
   * <p>如果有多个，随机选取一个返回
   *
   * @param type 密钥类型
   * @return Set<ClientSecret>
   */
  Optional<ClientSecret> getClientSecret(ClientSecretType type);

  /**
   * 客户端密钥
   *
   * @return String
   */
  Optional<ClientSecret> getClientSecret(String id);

  /**
   * 跳转地址
   *
   * @return String
   */
  String getRedirectUri();

  /**
   * 授权范围
   *
   * @return Set<String>
   */
  Set<String> getScope();
}
