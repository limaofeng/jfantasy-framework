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
package net.asany.jfantasy.framework.dao.jpa.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.asany.jfantasy.framework.dao.jpa.system.util.EntityRelationshipData;

public class EntityRelationshipManager {

  // 用于存储解析后的实体关系数据
  private final Map<Class<?>, List<EntityRelationshipData>> relationshipsMap = new HashMap<>();

  // 用于添加关系数据
  public void addRelationships(Class<?> entityClass, List<EntityRelationshipData> relationships) {
    relationshipsMap.put(entityClass, relationships);
  }

  // 查询实体之间的关系
  public List<EntityRelationshipData> getRelationships(Class<?> entityClass) {
    return relationshipsMap.get(entityClass);
  }

  /**
   * 查询实体之间的关系
   *
   * @param entityClass 实体类
   * @param name 关系名称
   * @return 关系数据
   */
  public Optional<EntityRelationshipData> getRelationship(Class<?> entityClass, String name) {
    return relationshipsMap.get(entityClass).stream()
        .filter(relationship -> relationship.getName().equals(name))
        .findFirst();
  }

  // 全局查询所有关系信息
  public Map<Class<?>, List<EntityRelationshipData>> getAllRelationships() {
    return relationshipsMap;
  }
}
