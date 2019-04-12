package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.hibernate.PropertyFilter;
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
 * @Description: TODO
 * @date 14/11/2017 10:01 AM
 */
public class PropertyFilterSpecification implements Specification {

    private List<PropertyFilter> filters;

    public PropertyFilterSpecification(List<PropertyFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder builder) {
        List<Predicate> restrictions = new ArrayList<>();
        for (PropertyFilter filter : filters) {
            restrictions.add(buildPropertyFilterPredicate(root, builder, filter.getPropertyName(), filter.getPropertyValue(), filter.getMatchType()));
        }
        return builder.and(restrictions.toArray(new Predicate[restrictions.size()]));
    }

    protected Predicate buildPropertyFilterPredicate(Root root, CriteriaBuilder builder, String propertyName, Object propertyValue, PropertyFilter.MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");

        Path path = root;
        for (String name : StringUtil.tokenizeToStringArray(propertyName, ".")) {
            Path tmp = path.get(name);
            if (Collection.class.isAssignableFrom(tmp.getJavaType())) {
                tmp = ((Root) path).join(name);
            }
            path = tmp;
        }
        if (PropertyFilter.MatchType.EQ.equals(matchType)) {
            return builder.equal(path, propertyValue);
        } else if (PropertyFilter.MatchType.LIKE.equals(matchType)) {
            return builder.like(path, (String) propertyValue);
        } else if (PropertyFilter.MatchType.LE.equals(matchType)) {
            return builder.le(path, (Number) propertyValue);
        } else if (PropertyFilter.MatchType.LT.equals(matchType)) {
            return builder.lt(path, (Number) propertyValue);
        } else if (PropertyFilter.MatchType.GE.equals(matchType)) {
            return builder.ge(path, (Number) propertyValue);
        } else if (PropertyFilter.MatchType.GT.equals(matchType)) {
            return builder.gt(path, (Number) propertyValue);
        } else if (PropertyFilter.MatchType.IN.equals(matchType)) {
            if (Array.getLength(propertyValue) == 0) {
                return null;
            }
            return builder.in(path.in((Object[]) propertyValue));
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
