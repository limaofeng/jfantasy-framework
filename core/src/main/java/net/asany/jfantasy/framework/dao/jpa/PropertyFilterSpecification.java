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
package net.asany.jfantasy.framework.dao.jpa;

import static net.asany.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

import jakarta.persistence.criteria.*;
import java.lang.reflect.Array;
import java.util.*;
import net.asany.jfantasy.framework.dao.MatchType;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

/**
 * @author limaofeng
 * @version V1.0 @Description: PropertyFilter 的 JPA 实现
 * @date 14/11/2017 10:01 AM
 */
public class PropertyFilterSpecification<T> implements Specification<T> {

  private final List<PropertyPredicate> filters;
  private final Class<?> entityClass;
  private final PropertyFilterSpecificationContext context;

  public PropertyFilterSpecification(Class<T> entityClass, List<PropertyPredicate> filters) {
    this.entityClass = entityClass;
    this.filters = filters;
    this.context = new PropertyFilterSpecificationContext();
  }

  private PropertyFilterSpecification(
      Class<?> entityClass,
      List<PropertyPredicate> filters,
      PropertyFilterSpecificationContext context) {
    this.entityClass = entityClass;
    this.filters = filters;
    this.context = context;
  }

  @Override
  public Predicate toPredicate(
      @NotNull Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder builder) {
    if (!query.isDistinct()) {
      query.distinct(true);
    }

    List<Predicate> predicates = new ArrayList<>();
    for (PropertyPredicate filter : filters) {
      if (filter.isSpecification()) {
        //noinspection unchecked
        predicates =
            join(
                predicates,
                filter.getPropertyValue(Specification.class).toPredicate(root, query, builder));
      } else if (filter.getMatchType() == MatchType.AND
          || filter.getMatchType() == MatchType.OR
          || filter.getMatchType() == MatchType.NOT) {
        //noinspection unchecked
        Predicate[] predicateChildren =
            buildPropertyFilterPredicate(filter.getPropertyValue(List.class), root, query, builder);
        if (predicateChildren.length == 0) {
          continue;
        }
        predicates =
            join(predicates, conjunction(filter.getMatchType(), builder, predicateChildren));
      } else {
        predicates =
            join(
                predicates,
                buildPropertyFilterPredicate(
                    root,
                    builder,
                    filter.getPropertyName(),
                    getPropertyValue(filter),
                    filter.getMatchType()));
      }
    }
    return conjunction(MatchType.AND, builder, predicates.toArray(new Predicate[0]));
  }

  private Predicate conjunction(
      MatchType matchType, CriteriaBuilder builder, Predicate[] predicates) {
    if (matchType == MatchType.NOT) {
      return builder.not(conjunction(MatchType.AND, builder, predicates));
    }
    if (predicates.length == 1) {
      return predicates[0];
    }
    if (matchType == MatchType.OR) {
      return builder.or(predicates);
    }
    return builder.and(predicates);
  }

  private Predicate[] buildPropertyFilterPredicate(
      List<List<PropertyPredicate>> filters,
      Root<T> root,
      CriteriaQuery<?> query,
      CriteriaBuilder builder) {
    try {
      return filters.stream()
          .map(item -> buildMultiplePropertyFilterPredicate(item, root, query, builder))
          .toArray(Predicate[]::new);
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  private Predicate buildMultiplePropertyFilterPredicate(
      List<PropertyPredicate> filters,
      Root<T> root,
      CriteriaQuery<?> query,
      CriteriaBuilder builder) {
    Specification<T> specification =
        new PropertyFilterSpecification<>(this.entityClass, filters, this.context);
    return specification.toPredicate(root, query, builder);
  }

  private List<Predicate> join(List<Predicate> predicates, Predicate y) {
    if (y == null) {
      return predicates;
    }
    predicates.add(y);
    return predicates;
  }

  public Object getPropertyValue(PropertyPredicate filter) {
    return filter.getPropertyValue(
        ClassUtil.getPropertyType(this.entityClass, filter.getPropertyName()));
  }

  protected Predicate buildPropertyFilterPredicate(
      Root<T> root,
      CriteriaBuilder builder,
      String propertyName,
      Object propertyValue,
      MatchType matchType) {
    Assert.hasText(propertyName, "propertyName不能为空");

    //noinspection rawtypes
    Path path = this.context.path(root, propertyName);

    if (MatchType.EQ == matchType) {
      return builder.equal(path, propertyValue);
    } else if (MatchType.CONTAINS == matchType) {
      //noinspection unchecked
      return builder.like(path, '%' + (String) propertyValue + '%');
    } else if (MatchType.NOT_CONTAINS == matchType) {
      //noinspection unchecked
      return builder.notLike(path, '%' + (String) propertyValue + '%');
    } else if (MatchType.STARTS_WITH == matchType) {
      //noinspection unchecked
      return builder.like(path, (String) propertyValue + '%');
    } else if (MatchType.NOT_STARTS_WITH == matchType) {
      //noinspection unchecked
      return builder.notLike(path, (String) propertyValue + '%');
    } else if (MatchType.ENDS_WITH == matchType) {
      //noinspection unchecked
      return builder.like(path, '%' + (String) propertyValue);
    } else if (MatchType.NOT_ENDS_WITH == matchType) {
      //noinspection unchecked
      return builder.notLike(path, '%' + (String) propertyValue);
    } else if (MatchType.LTE == matchType) {
      //noinspection unchecked
      return builder.lessThanOrEqualTo(path, (Comparable<Object>) propertyValue);
    } else if (MatchType.LT == matchType) {
      //noinspection unchecked
      return builder.lessThan(path, (Comparable<Object>) propertyValue);
    } else if (MatchType.GTE == matchType) {
      //noinspection unchecked
      return builder.greaterThanOrEqualTo(path, (Comparable<Object>) propertyValue);
    } else if (MatchType.GT == matchType) {
      //noinspection unchecked
      return builder.greaterThan(path, (Comparable<Object>) propertyValue);
    } else if (MatchType.IN == matchType) {
      return path.in(Arrays.stream(multipleValuesObjectsObjects(propertyValue)).toArray());
    } else if (MatchType.NOT_IN == matchType) {
      return builder.not(
          path.in(Arrays.stream(multipleValuesObjectsObjects(propertyValue)).toArray()));
    } else if (MatchType.NOT_EQUAL == matchType) {
      return builder.notEqual(path, propertyValue);
    } else if (MatchType.NULL == matchType) {
      return builder.isNull(path);
    } else if (MatchType.NOT_NULL == matchType) {
      return builder.isNotNull(path);
    } else if (MatchType.EMPTY == matchType) {
      //noinspection unchecked
      return builder.isEmpty(path);
    } else if (MatchType.NOT_EMPTY == matchType) {
      //noinspection unchecked
      return builder.isNotEmpty(path);
    } else if (MatchType.BETWEEN == matchType) {
      //noinspection unchecked
      Comparable<Object> x = (Comparable<Object>) Array.get(propertyValue, 0);
      //noinspection unchecked
      Comparable<Object> y = (Comparable<Object>) Array.get(propertyValue, 1);
      // noinspection unchecked
      return builder.between(path, x, y);
    }
    throw new RuntimeException("不支持的查询");
  }

  static class PropertyFilterSpecificationContext {

    private final Map<String, Path<?>> paths = new HashMap<>();
    private int rootHashCode = 0;

    public Path<?> path(Root<?> root, String propertyName) {
      if (root.hashCode() != rootHashCode) {
        paths.clear();
        rootHashCode = root.hashCode();
      }

      Path<?> path = root;
      String[] propertyNames = StringUtil.tokenizeToStringArray(propertyName, ".");
      for (int i = 0; i < propertyNames.length; i++) {
        String name = propertyNames[i];
        String key = StringUtil.join(Arrays.copyOfRange(propertyNames, 0, i + 1), ".");

        if (paths.containsKey(key)) {
          path = paths.get(key);
          continue;
        }

        Path<?> tmp;
        try {
          tmp = path.get(name);
          if (!ClassUtil.isBasicType(tmp.getJavaType())) {
            tmp = ((From<?, ?>) path).join(name, JoinType.LEFT);
            paths.put(key, tmp);
          }
        } catch (ClassCastException e) {
          tmp = ((From<?, ?>) path).join(name, JoinType.LEFT);
          paths.put(key, tmp);
        }
        path = tmp;
      }
      return path;
    }
  }
}
