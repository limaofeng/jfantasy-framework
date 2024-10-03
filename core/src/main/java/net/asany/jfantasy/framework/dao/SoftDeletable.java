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
 * 逻辑删除接口
 *
 * @author limaofeng
 */
public interface SoftDeletable {

  Map<Class<?>, String> filedNameCache = new ConcurrentHashMap<>();
  String DELETED_BY_FIELD_NAME = "deleted";

  /**
   * 是否已经删除
   *
   * @param b 是否已经删除
   */
  void setDeleted(boolean b);

  /**
   * 是否已经删除
   *
   * @return 是否已经删除
   */
  boolean isDeleted();

  /**
   * 是否已经删除
   *
   * @param domainClass 实体类
   * @return 是否已经删除
   */
  static String getDeletedFieldName(Class<?> domainClass) {
    return filedNameCache.computeIfAbsent(
        domainClass,
        key -> {
          try {
            return ClassUtil.getFieldValue(domainClass, "DELETED_BY_FIELD_NAME");
          } catch (Exception e) {
            return DELETED_BY_FIELD_NAME;
          }
        });
  }
}
