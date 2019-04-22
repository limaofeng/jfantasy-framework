package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ClassUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-22 17:55
 */
public class PropertyFilterBuilder {

    private Class<?> entityClass;
    private List<PropertyFilter> filters = new ArrayList<>();

    public PropertyFilterBuilder() {
    }

    public PropertyFilterBuilder(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public List<PropertyFilter> build() {
        return this.filters;
    }

    private void setPropertyFilterType(PropertyFilter filter, String name) {
        if (this.entityClass != null) {
            filter.setPropertyType(ClassUtil.getPropertyType(this.entityClass, name));
        }
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
        PropertyFilter filter = new PropertyFilter("EQ_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * 模糊查询
     */
    public PropertyFilterBuilder contains(String name, String value) {
        PropertyFilter filter = new PropertyFilter("LIKE_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue('%' + value + "%");
        this.filters.add(filter);
        return this;
    }

    /**
     * 小于
     */
    public <T> PropertyFilterBuilder lessThan(String name, T value) {
        PropertyFilter filter = new PropertyFilter("LT_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * 大于
     */
    public PropertyFilterBuilder greaterThan(String name, Object value) {
        PropertyFilter filter = new PropertyFilter("GT_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * 小于等于
     */
    public PropertyFilterBuilder lessThanOrEqual(String name, Object value) {
        PropertyFilter filter = new PropertyFilter("LE_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * 大于等于
     */
    public PropertyFilterBuilder greaterThanOrEqual(String name, Object value) {
        PropertyFilter filter = new PropertyFilter("GE_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * in
     */
    public <T> PropertyFilterBuilder in(String name, T... value) {
        PropertyFilter filter = new PropertyFilter("IN_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * not in
     */
    public <T> PropertyFilterBuilder notIn(String name, T... value) {
        PropertyFilter filter = new PropertyFilter("NOTIN_" + name);
        setPropertyFilterType(filter, name);
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * 不等于
     */
    public PropertyFilterBuilder notEqual(String name, Object... value) {
        PropertyFilter filter = new PropertyFilter("NE_" + name);
        if (this.entityClass != null) {
            filter.setPropertyType(ClassUtil.getPropertyType(this.entityClass, name));
        }
        filter.setPropertyValue(value);
        this.filters.add(filter);
        return this;
    }

    /**
     * is null
     */
    public PropertyFilterBuilder isNull(String name) {
        this.filters.add(new PropertyFilter("NULL_" + name));
        return this;
    }

    /**
     * not null
     */
    public PropertyFilterBuilder isNotNull(String name) {
        this.filters.add(new PropertyFilter("NOTNULL_" + name));
        return this;
    }

    /**
     *
     */
    public PropertyFilterBuilder isEmpty(String name) {
        this.filters.add(new PropertyFilter("EMPTY_" + name));
        return this;
    }

    /**
     *
     */
    public PropertyFilterBuilder isNotEmpty(String name) {
        this.filters.add(new PropertyFilter("NOTEMPTY_" + name));
        return this;
    }

    public <Y extends Comparable<? super Y>> PropertyFilterBuilder between(String name, Y x, Y y) {
        PropertyFilter filter = new PropertyFilter("BETWEEN_" + name);
        if (this.entityClass != null) {
            filter.setPropertyType(ClassUtil.getPropertyType(this.entityClass, name));
        }
        filter.setPropertyValue(new Object[]{x, y});
        this.filters.add(filter);
        return this;
    }

    public PropertyFilterBuilder sql(String name) {
        return this;
    }
}
