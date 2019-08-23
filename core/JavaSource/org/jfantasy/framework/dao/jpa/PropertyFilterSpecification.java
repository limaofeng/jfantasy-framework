package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.dao.hibernate.PropertyFilter.MatchType;
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
        Predicate predicate = null;
        List<Specification> andExpressions = conjunction(MatchType.AND);
        List<Specification> orExpressions = conjunction(MatchType.OR);
        for (PropertyFilter filter : filters) {
            if (filter.getMatchType() == MatchType.AND || filter.getMatchType() == MatchType.OR) {
                continue;
            }
            Predicate condition = buildPropertyFilterPredicate(root, builder, filter.getPropertyName(), getPropertyValue(filter), filter.getMatchType());
            if (predicate == null) {
                predicate = condition;
            } else {
                predicate = builder.and(predicate, condition);
            }
        }
        if (predicate == null) {
            predicate = builder.and();
        }
        for (Specification specification : andExpressions) {
            predicate = builder.and(predicate, specification.toPredicate(root, query, builder));
        }
        for (Specification specification : orExpressions) {
            predicate = builder.or(predicate, specification.toPredicate(root, query, builder));
        }
        return predicate;
    }

    private List<Specification> conjunction(MatchType matchType) {
        return filters.stream().filter(item -> item.getMatchType() == matchType).map(item -> {
            if (item.isSpecification()) {
                return (Specification) item.getPropertyValue();
            }
            return new PropertyFilterSpecification(this.entityClass, item.getPropertyValue());
        }).collect(Collectors.toList());
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
