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
import net.asany.jfantasy.framework.dao.jpa.system.domain.EntityRelationship;
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
      @NotNull Root root, @NotNull CriteriaQuery query, @NotNull CriteriaBuilder cb) {
    Class<?> entityClass = root.getModel().getJavaType();

    String[] types = new String[] {"device", "store", "customer"};

    // First join: Find the store to which the device belongs
    Root<EntityRelationship> storeRelationshipRoot = query.from(EntityRelationship.class);
    Predicate deviceToStoreJoin = cb.equal(root.get("id"), storeRelationshipRoot.get("childId"));
    Predicate storeChildTypeCondition = cb.equal(storeRelationshipRoot.get("childType"), "device");
    Predicate storeParentTypeCondition = cb.equal(storeRelationshipRoot.get("parentType"), "store");

    // Second join: Find the customer to which the store belongs
    Root<EntityRelationship> customerRelationshipRoot = query.from(EntityRelationship.class);
    Predicate storeToCustomerJoin =
        cb.equal(storeRelationshipRoot.get("parentId"), customerRelationshipRoot.get("childId"));
    Predicate customerChildTypeCondition =
        cb.equal(customerRelationshipRoot.get("childType"), "store");
    Predicate customerParentTypeCondition =
        cb.equal(customerRelationshipRoot.get("parentType"), "customer");
    Predicate customerCondition =
        cb.equal(customerRelationshipRoot.get("parentId"), relatedEntityId);

    // Combine all predicates
    Predicate finalCondition =
        cb.and(
            deviceToStoreJoin,
            storeChildTypeCondition,
            storeParentTypeCondition,
            storeToCustomerJoin,
            customerChildTypeCondition,
            customerParentTypeCondition,
            customerCondition);

    //    PropertyFilter filter;
    //    filter.unwarp(JpaDefaultPropertyFilter.class).and(this);

    return finalCondition;
  }
}
