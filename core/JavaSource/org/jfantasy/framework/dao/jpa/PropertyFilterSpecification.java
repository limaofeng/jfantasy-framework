package org.jfantasy.framework.dao.jpa;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.reflect.Property;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 14/11/2017 10:01 AM
 */
public class PropertyFilterSpecification implements Specification {

    private List<PropertyFilter> filters;

    public PropertyFilterSpecification(List<PropertyFilter> filters){
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder builder) {
        List<Predicate> restrictions = new ArrayList<>();
        for(PropertyFilter filter : filters){
            restrictions.add(buildPropertyFilterPredicate(root,builder,filter.getPropertyName(),filter.getPropertyValue(),filter.getMatchType()));
        }
        return builder.and(restrictions.toArray(new Predicate[restrictions.size()]));
    }

    protected Predicate buildPropertyFilterPredicate(Root root,CriteriaBuilder builder, String propertyName, Object propertyValue, PropertyFilter.MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");
        if (PropertyFilter.MatchType.EQ.equals(matchType)) {
            return builder.equal(root.get(propertyName),propertyValue);
        } else if (PropertyFilter.MatchType.LIKE.equals(matchType)) {
            return builder.like(root.get(propertyName), (String) propertyValue);
        } else if (PropertyFilter.MatchType.LE.equals(matchType)) {
            return builder.le(root.get(propertyName), (Number) propertyValue);
        } else if (PropertyFilter.MatchType.LT.equals(matchType)) {
            return builder.lt(root.get(propertyName), (Number) propertyValue);
        } else if (PropertyFilter.MatchType.GE.equals(matchType)) {
            return builder.ge(root.get(propertyName), (Number) propertyValue);
        } else if (PropertyFilter.MatchType.GT.equals(matchType)) {
            return builder.gt(root.get(propertyName), (Number) propertyValue);
        } else if (PropertyFilter.MatchType.IN.equals(matchType)) {
            if (Array.getLength(propertyValue) == 0) {
                return null;
            }
            return builder.in(root.get(propertyName).in((Object[])propertyValue));
        } else if (PropertyFilter.MatchType.NOTIN.equals(matchType)) {
            if (Array.getLength(propertyValue) == 0) {
                return null;
            }
            return builder.not(root.get(propertyName).in((Object[])propertyValue));
        } else if (PropertyFilter.MatchType.NE.equals(matchType)) {
            return builder.notEqual(root.get(propertyName),propertyValue);
        } else if (PropertyFilter.MatchType.NULL.equals(matchType)) {
            return builder.isNull(root.get(propertyName));
        } else if (PropertyFilter.MatchType.NOTNULL.equals(matchType)) {
            return builder.isNotNull(root.get(propertyName));
        } else if (PropertyFilter.MatchType.EMPTY.equals(matchType)) {
            return builder.isEmpty(root.get(propertyName));
        } else if (PropertyFilter.MatchType.NOTEMPTY.equals(matchType)) {
            return builder.isNotEmpty(root.get(propertyName));
        } else if (PropertyFilter.MatchType.BETWEEN.equals(matchType)) {
            Comparable x = (Comparable) Array.get(propertyValue, 0);
            Comparable y = (Comparable) Array.get(propertyValue, 1);
            return builder.between(root.get(propertyName),x,y);
        }
        throw new RuntimeException("不支持的查询");
    }


}
