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
package net.asany.jfantasy.framework.dao.jpa.system;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class EntityRelationshipSpecification implements Specification<Object> {
  private final Long relatedEntityId;
  private final String[] names;

  public EntityRelationshipSpecification(Long relatedEntityId, String... names) {
    this.relatedEntityId = relatedEntityId;
    this.names = names;
  }

  @Override
  public Predicate toPredicate(
      @NotNull Root root, @NotNull CriteriaQuery query, @NotNull CriteriaBuilder criteriaBuilder) {
    Class<?> entityClass = root.getModel().getJavaType();

    return null;
  }
}
