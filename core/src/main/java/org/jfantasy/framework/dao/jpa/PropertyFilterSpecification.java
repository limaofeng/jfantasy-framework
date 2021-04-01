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
//        query.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        for (PropertyFilter filter : filters) {
            if (filter.isSpecification()) {
                join(predicates, filter.getPropertyValue(Specification.class).toPredicate(root, query, builder));
            } else if (filter.getMatchType() == MatchType.AND || filter.getMatchType() == MatchType.OR) {
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
        if (predicates.length == 1) {
            return predicates[0];
        }
        return matchType == MatchType.AND ? builder.and(predicates) : builder.or(predicates);
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

    protected Predicate buildPropertyFilterPredicate(Root root, CriteriaBuilder builder, String propertyName, Object propertyValue, PropertyFilter.MatchType matchType) {
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

        if (PropertyFilter.MatchType.EQ.equals(matchType)) {
            return builder.equal(path, propertyValue);
        } else if (PropertyFilter.MatchType.LIKE.equals(matchType)) {
            return builder.like(path, (String) propertyValue);
        } else if (PropertyFilter.MatchType.LE.equals(matchType)) {
            return builder.lessThanOrEqualTo(path, (Comparable) propertyValue);
        } else if (PropertyFilter.MatchType.LT.equals(matchType)) {
            return builder.lessThan(path, (Comparable) propertyValue);
        } else if (PropertyFilter.MatchType.GE.equals(matchType)) {
            return builder.greaterThanOrEqualTo(path, (Comparable) propertyValue);
        } else if (PropertyFilter.MatchType.GT.equals(matchType)) {
            return builder.greaterThan(path, (Comparable) propertyValue);
        } else if (PropertyFilter.MatchType.IN.equals(matchType)) {
            if (ClassUtil.isArray(propertyValue)) {
                return path.in((Object[]) propertyValue);
            }
            if (ClassUtil.isList(propertyValue)) {
                return path.in((Collection<?>) propertyValue);
            }
            return path.in(propertyValue);
        } else if (PropertyFilter.MatchType.NOTIN.equals(matchType)) {
            if (ClassUtil.isArray(propertyValue)) {
                return builder.not(path.in((Object[]) propertyValue));
            }
            if (ClassUtil.isList(propertyValue)) {
                return builder.not(path.in((Collection<?>) propertyValue));
            }
            return builder.not(path.in(propertyValue));
        } else if (PropertyFilter.MatchType.NE.equals(matchType)) {
            return builder.notEqual(path, propertyValue);
        } else if (PropertyFilter.MatchType.NULL.equals(matchType)) {
            return builder.isNull(path);
        } else if (PropertyFilter.MatchType.NOTNULL.equals(matchType)) {
            return builder.isNotNull(path);
        } else if (PropertyFilter.MatchType.EMPTY.equals(matchType)) {
            return builder.isEmpty(path);
        } else if (PropertyFilter.MatchType.NOTEMPTY.equals(matchType)) {
            return builder.isNotEmpty(path);
        } else if (PropertyFilter.MatchType.BETWEEN.equals(matchType)) {
            Comparable x = (Comparable) Array.get(propertyValue, 0);
            Comparable y = (Comparable) Array.get(propertyValue, 1);
            return builder.between(path, x, y);
        }
        throw new RuntimeException("不支持的查询");
    }

}
