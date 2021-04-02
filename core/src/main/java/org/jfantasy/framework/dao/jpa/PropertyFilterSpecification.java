package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.jpa.PropertyFilter.MatchType;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: PropertyFilter 的 JPA 实现
 * @date 14/11/2017 10:01 AM
 */
public class PropertyFilterSpecification implements Specification {

    private List<PropertyFilter> filters;
    private Class<?> entityClass;

    public PropertyFilterSpecification(Class<?> entityClass, List<PropertyFilter> filters) {
        this.entityClass = entityClass;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        for (PropertyFilter filter : filters) {
            if (filter.isSpecification()) {
                join(predicates, filter.getPropertyValue(Specification.class).toPredicate(root, query, builder));
            } else if (filter.getMatchType() == MatchType.AND || filter.getMatchType() == MatchType.OR || filter.getMatchType() == MatchType.NOT) {
                Predicate[] predicateChildren = buildPropertyFilterPredicate(filter.getPropertyValue(List.class), root, query, builder);
                if (predicateChildren.length == 0) {
                    continue;
                }
                join(predicates, conjunction(filter.getMatchType(), builder, predicateChildren));
            } else {
                join(predicates, buildPropertyFilterPredicate(root, builder, filter.getPropertyName(), getPropertyValue(filter), filter.getMatchType()));
            }
        }
        return conjunction(MatchType.AND, builder, predicates.stream().toArray(Predicate[]::new));
    }

    private Predicate conjunction(MatchType matchType, CriteriaBuilder builder, Predicate[] predicates) {
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

    private Predicate[] buildPropertyFilterPredicate(List<List<PropertyFilter>> filters, Root root, CriteriaQuery query, CriteriaBuilder builder) {
        return filters.stream().map(item -> buildMultiplePropertyFilterPredicate(item, root, query, builder)).toArray(Predicate[]::new);
    }

    private Predicate buildMultiplePropertyFilterPredicate(List<PropertyFilter> filters, Root root, CriteriaQuery query, CriteriaBuilder builder) {
        Specification specification = new PropertyFilterSpecification(this.entityClass, filters);
        return specification.toPredicate(root, query, builder);
    }

    private List<Predicate> join(List<Predicate> predicates, Predicate y) {
        if (y == null) {
            return predicates;
        }
        predicates.add(y);
        return predicates;
    }

    public Object getPropertyValue(PropertyFilter filter) {
        return filter.getPropertyValue(ClassUtil.getPropertyType(this.entityClass, filter.getPropertyName()));
    }

    protected Predicate buildPropertyFilterPredicate(Root root, CriteriaBuilder builder, String propertyName, Object propertyValue, MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");

        Path path = root;
        String[] propertyNames = StringUtil.tokenizeToStringArray(propertyName, ".");
        for (String name : propertyNames) {
            Path tmp = path.get(name);
            if (!ClassUtil.isBasicType(tmp.getJavaType())) {
                tmp = ((From) path).join(name, JoinType.LEFT);
            }
            path = tmp;
        }

        if (MatchType.EQ == matchType) {
            return builder.equal(path, propertyValue);
        } else if (MatchType.CONTAINS == matchType) {
            return builder.like(path, '%' + (String) propertyValue + '%');
        } else if (MatchType.NOT_CONTAINS == matchType) {
            return builder.notLike(path, '%' + (String) propertyValue + '%');
        } else if (MatchType.STARTS_WITH == matchType) {
            return builder.like(path, (String) propertyValue + '%');
        } else if (MatchType.NOT_STARTS_WITH == matchType) {
            return builder.notLike(path, (String) propertyValue + '%');
        } else if (MatchType.ENDS_WITH == matchType) {
            return builder.like(path, '%' + (String) propertyValue);
        } else if (MatchType.NOT_ENDS_WITH == matchType) {
            return builder.notLike(path, '%' + (String) propertyValue);
        } else if (MatchType.LTE == matchType || MatchType.LE == matchType) {
            return builder.lessThanOrEqualTo(path, (Comparable) propertyValue);
        } else if (MatchType.LT == matchType) {
            return builder.lessThan(path, (Comparable) propertyValue);
        } else if (MatchType.GTE == matchType || MatchType.GE == matchType) {
            return builder.greaterThanOrEqualTo(path, (Comparable) propertyValue);
        } else if (MatchType.GT == matchType) {
            return builder.greaterThan(path, (Comparable) propertyValue);
        } else if (MatchType.IN == matchType) {
            return path.in(multipleValuesObjectsObjects(propertyValue));
        } else if (MatchType.NOT_IN == matchType || MatchType.NOTIN == matchType) {
            return builder.not(path.in(multipleValuesObjectsObjects(propertyValue)));
        } else if (MatchType.NOT_EQUAL == matchType || MatchType.NE == matchType) {
            return builder.notEqual(path, propertyValue);
        } else if (MatchType.NULL == matchType) {
            return builder.isNull(path);
        } else if (MatchType.NOT_NULL == matchType || MatchType.NOTNULL == matchType) {
            return builder.isNotNull(path);
        } else if (MatchType.EMPTY == matchType) {
            return builder.isEmpty(path);
        } else if (MatchType.NOT_EMPTY == matchType || MatchType.NOTEMPTY == matchType) {
            return builder.isNotEmpty(path);
        } else if (MatchType.BETWEEN == matchType) {
            Comparable x = (Comparable) Array.get(propertyValue, 0);
            Comparable y = (Comparable) Array.get(propertyValue, 1);
            return builder.between(path, x, y);
        } else if (MatchType.LIKE == matchType) {
            return builder.like(path, (String) propertyValue);
        }
        throw new RuntimeException("不支持的查询");
    }

}
