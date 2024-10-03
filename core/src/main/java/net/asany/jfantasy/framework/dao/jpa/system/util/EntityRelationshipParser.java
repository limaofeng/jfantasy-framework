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
package net.asany.jfantasy.framework.dao.jpa.system.util;

import java.util.ArrayList;
import java.util.List;
import net.asany.jfantasy.framework.dao.hibernate.annotations.EntityRelationship;
import net.asany.jfantasy.framework.dao.hibernate.annotations.Relationship;
import net.asany.jfantasy.framework.dao.hibernate.annotations.RelationshipType;

public class EntityRelationshipParser {

  public List<String> parseRelationships(Class<?> entityClass) {
    List<String> relationshipsInfo = new ArrayList<>();

    // 检查实体是否包含 @EntityRelationship 注解
    if (entityClass.isAnnotationPresent(EntityRelationship.class)) {
      EntityRelationship entityRelationship = entityClass.getAnnotation(EntityRelationship.class);
      Relationship[] relationships = entityRelationship.value();

      for (Relationship relationship : relationships) {
        RelationshipType relationshipType = relationship.type();
        Class<?> relatedEntity = relationship.relatedEntity();
        relationshipsInfo.add(
            "Entity: "
                + entityClass.getSimpleName()
                + ", Relationship: "
                + relationshipType
                + ", Related Entity: "
                + relatedEntity.getSimpleName());
      }
    }

    return relationshipsInfo;
  }
}
