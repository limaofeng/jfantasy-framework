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
package net.asany.jfantasy.framework.dao.jpa.system.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asany.jfantasy.framework.dao.BaseBusEntity;
import net.asany.jfantasy.framework.dao.hibernate.annotations.RelationshipType;
import net.asany.jfantasy.framework.dao.hibernate.annotations.TableGenerator;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(
    name = "SYS_ENTITY_RELATIONSHIP",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {
            "SOURCE_ID",
            "SOURCE_TYPE",
            "RELATIONSHIP_TYPE",
            "TARGET_ID",
            "TARGET_TYPE"
          })
    })
public class EntityRelationship extends BaseBusEntity {

  @Id
  @Column(name = "ID")
  @TableGenerator
  private Long id;

  @Column(name = "SOURCE_ID")
  private Long sourceId;

  @Column(name = "SOURCE_TYPE", length = 120)
  private String sourceType;

  @Enumerated(EnumType.STRING)
  @Column(name = "RELATIONSHIP_TYPE", length = 20)
  private RelationshipType relationship;

  @Column(name = "TARGET_ID")
  private Long targetId;

  @Column(name = "TARGET_TYPE", length = 120)
  private String targetType;
}
