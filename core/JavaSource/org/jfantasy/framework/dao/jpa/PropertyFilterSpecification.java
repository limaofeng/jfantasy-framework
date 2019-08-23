package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.jpa.PropertyFilter.MatchType;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
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
        query.distinct(true);
        List<Predicate> andPredicates = this.getPredicates(MatchType.AND, root, query, builder);
        List<Predicate> orPredicates = this.getPredicates(MatchType.OR, root, query, builder);

        Predicate rootPredicate = null;
        for (PropertyFilter filter : filters) {
            if (filter.getMatchType() == MatchType.AND || filter.getMatchType() == MatchType.OR) {
                continue;
            }
            Predicate condition = buildPropertyFilterPredicate(root, builder, filter.getPropertyName(), getPropertyValue(filter), filter.getMatchType());
            rootPredicate = this.conjunction(MatchType.AND, builder, rootPredicate, condition);
        }

        rootPredicate = this.conjunction(MatchType.AND, builder, rootPredicate, andPredicates);
        rootPredicate = this.conjunction(MatchType.OR, builder, rootPredicate, orPredicates);
        return rootPredicate;
    }

    private List<Predicate> getPredicates(MatchType matchType, Root root, CriteriaQuery query, CriteriaBuilder builder) {
        return filters.stream().filter(item -> item.getMatchType() == matchType).map(item -> {
            if (item.isSpecification()) {
                return (Specification) item.getPropertyValue();
            }
            return new PropertyFilterSpecification(this.entityClass, item.getPropertyValue());
        }).map(item -> item.toPredicate(root, query, builder)).collect(Collectors.toList());
    }

    private Predicate conjunction(MatchType matchType, CriteriaBuilder builder, Predicate x, Predicate y) {
        if (x == null) {
            return y;
        }
        return matchType == MatchType.AND ? builder.and(x, y) : builder.or(x, y);
    }

    private Predicate conjunction(MatchType matchType, CriteriaBuilder builder, Predicate x, List<Predicate> predicates) {
        for (Predicate y : predicates) {
            x = this.conjunction(matchType, builder, x, y);
        }
        return x;
    }

    public Object getPropertyValue(PropertyFilter filter) {
        return filter.getPropertyValue(ClassUtil.getPropertyType(this.entityClass, filter.getPropertyName()));
    }

    protected Predicate buildPropertyFilterPredicate(Root root, CriteriaBuilder builder, String propertyName, Object propertyValue, PropertyFilter.MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");

        Path path = root;
        for (String name : StringUtil.tokenizeToStringArray(propertyName, ".")) {
            Path tmp = path.get(name);
            if (Collection.class.isAssignableFrom(tmp.getJavaType()) && !propertyName.endsWith(name)) {
                tmp = ((Root) path).join(name, JoinType.LEFT);
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
            if (Array.getLength(propertyValue) == 0) {
                return null;
            }
            return path.in((Object[]) propertyValue);
        } else if (PropertyFilter.MatchType.NOTIN.equals(matchType)) {
            if (Array.getLength(propertyValue) == 0) {
                return null;
            }
            return builder.not(path.in((Object[]) propertyValue));
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
