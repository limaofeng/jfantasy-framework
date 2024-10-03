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
package net.asany.jfantasy.framework.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.asany.jfantasy.framework.util.common.ClassUtil;

/**
 * 支持多租户属性的实体接口。
 *
 * @author limaofeng
 */
public interface Tenantable {

  Map<Class<?>, String> filedNameCache = new ConcurrentHashMap<>();
  String TENANT_BY_FIELD_NAME = "tenantId";

  static String getTenantFieldName(Class<?> domainClass) {
    return filedNameCache.computeIfAbsent(
        domainClass,
        key -> {
          try {
            return ClassUtil.getFieldValue(domainClass, "TENANT_BY_FIELD_NAME");
          } catch (Exception e) {
            return TENANT_BY_FIELD_NAME;
          }
        });
  }

  /**
   * 获取实体所属的租户ID。
   *
   * @return 租户ID
   */
  String getTenantId();

  /**
   * 设置实体所属的租户ID。
   *
   * @param tenantId 租户ID
   */
  void setTenantId(String tenantId);
}
