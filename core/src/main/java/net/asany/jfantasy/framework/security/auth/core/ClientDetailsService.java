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

/**
 * 客户端查询服务
 *
 * @author limaofeng
 */
public interface ClientDetailsService {
  /**
   * 查询客户端信息
   *
   * @param clientId 客户ID
   * @return ClientDetails
   * @throws ClientRegistrationException 异常
   */
  ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException;
}
