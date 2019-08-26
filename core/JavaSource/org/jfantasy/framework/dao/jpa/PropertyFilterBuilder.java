package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.jpa.PropertyFilter.MatchType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-22 17:55
 */
public class PropertyFilterBuilder {

    private List<PropertyFilter> filters = new ArrayList<>();

    public PropertyFilterBuilder() {
    }

    public List<PropertyFilter> build() {
        return this.filters;
    }

    /**
     * 等于
     *
     * @param name
     * @param value
     * @param <T>
     * @return
     */
    public <T> PropertyFilterBuilder equal(String name, T value) {
        this.filters.add(new PropertyFilter(MatchType.EQ, name, value));
        return this;
    }

    /**
     * 模糊查询
     */
    public PropertyFilterBuilder contains(String name, String value) {
        this.filters.add(new PropertyFilter(MatchType.LIKE, name, value));
        return this;
    }

    /**
     * 小于
     */
    public <T> PropertyFilterBuilder lessThan(String name, T value) {
        this.filters.add(new PropertyFilter(MatchType.LT, name, value));
        return this;
    }

    /**
     * 大于
     */
    public PropertyFilterBuilder greaterThan(String name, Object value) {
        this.filters.add(new PropertyFilter(MatchType.GT, name, value));
        return this;
    }

    /**
     * 小于等于
     */
    public PropertyFilterBuilder lessThanOrEqual(String name, Object value) {
        this.filters.add(new PropertyFilter(MatchType.LE, name, value));
        return this;
    }

    /**
     * 大于等于
     */
    public PropertyFilterBuilder greaterThanOrEqual(String name, Object value) {
        this.filters.add(new PropertyFilter(MatchType.GE, name, value));
        return this;
    }

    /**
     * in
     */
    public <T> PropertyFilterBuilder in(String name, T... value) {
        this.filters.add(new PropertyFilter(MatchType.IN, name, value));
        return this;
    }

    /**
     *
     * @param name
     * @param value
     * @param <T>
     * @return
     */
    public <T> PropertyFilterBuilder in(String name, List<T> value) {
        this.filters.add(new PropertyFilter(MatchType.IN, name, value));
        return this;
    }

    /**
     * not in
     */
    public <T> PropertyFilterBuilder notIn(String name, T... value) {
        this.filters.add(new PropertyFilter(MatchType.NOTIN, name, value));
        return this;
    }

    /**
     * 不等于
     */
    public <T> PropertyFilterBuilder notEqual(String name, T value) {
        this.filters.add(new PropertyFilter(MatchType.NE, name, value));
        return this;
    }

    /**
     * is null
     */
    public PropertyFilterBuilder isNull(String name) {
        this.filters.add(new PropertyFilter(MatchType.NULL, name));
        return this;
    }

    /**
     * not null
     */
    public PropertyFilterBuilder isNotNull(String name) {
        this.filters.add(new PropertyFilter(MatchType.NOTNULL, name));
        return this;
    }

    /**
     *
     */
    public PropertyFilterBuilder isEmpty(String name) {
        this.filters.add(new PropertyFilter(MatchType.EMPTY, name));
        return this;
    }

    /**
     *
     */
    public PropertyFilterBuilder isNotEmpty(String name) {
        this.filters.add(new PropertyFilter(MatchType.NOTEMPTY, name));
        return this;
    }

    public <Y extends Comparable<? super Y>> PropertyFilterBuilder between(String name, Y x, Y y) {
        this.filters.add(new PropertyFilter(MatchType.BETWEEN, name, x, y));
        return this;
    }

    public PropertyFilterBuilder and(PropertyFilterBuilder builder) {
        this.filters.add(new PropertyFilter(MatchType.AND, builder.build()));
        return this;
    }

    public PropertyFilterBuilder and(List<PropertyFilter> filters) {
        this.filters.add(new PropertyFilter(MatchType.AND, filters));
        return this;
    }

    public PropertyFilterBuilder and(Specification specification) {
        this.filters.add(new PropertyFilter(MatchType.AND, specification));
        return this;
    }

    public PropertyFilterBuilder or(List<PropertyFilter> filters) {
        this.filters.add(new PropertyFilter(MatchType.OR, filters));
        return this;
    }

    public PropertyFilterBuilder or(PropertyFilterBuilder builder) {
        this.filters.add(new PropertyFilter(MatchType.OR, builder.build()));
        return this;
    }

    public PropertyFilterBuilder or(Specification specification) {
        this.filters.add(new PropertyFilter(MatchType.OR, specification));
        return this;
    }

}
