package org.jfantasy.graphql.inputs;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.dao.jpa.PropertyFilter.MatchType;
import org.jfantasy.framework.dao.jpa.PropertyFilterBuilder;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

/**
 * Input Filter 查询根类
 *
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2020/4/14 10:07 上午
 */
public abstract class QueryFilter<F extends QueryFilter, T> {

    private final Class<T> entityClass;
    private final Map<String, TypeConverter> fields = new HashMap();

    protected PropertyFilterBuilder builder = new PropertyFilterBuilder();

    public QueryFilter() {
        this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
        if (this.entityClass == Object.class) {
            return;
        }
        for (Field field : ClassUtil.getDeclaredFields(this.entityClass)) {
            if (ClassUtil.isBasicType(field.getType())) {
                fields.put(field.getName(), new DefaultTypeConverter(field.getType()));
            }
        }
    }

    protected void register(String name, TypeConverter converter) {
        this.fields.put(name, converter);
    }

    @JsonProperty("AND")
    public void setAnd(F[] filters) {
        builder.and(Arrays.stream(filters).map(item -> item.builder).toArray(PropertyFilterBuilder[]::new));
    }

    @JsonProperty("OR")
    public void setOr(F[] filters) {
        builder.or(Arrays.stream(filters).map(item -> item.builder).toArray(PropertyFilterBuilder[]::new));
    }

    @JsonProperty("NOT")
    public void setNot(F[] filters) {
        builder.not(Arrays.stream(filters).map(item -> item.builder).toArray(PropertyFilterBuilder[]::new));
    }

    @JsonAnySetter
    public void set(String name, Object value) {
        String[] slugs = StringUtil.tokenizeToStringArray(name, "_");
        Object newValue;
        if (slugs.length > 1 && ObjectUtil.exists(new String[]{"in", "notIn"}, slugs[1])) {
            newValue = Arrays.stream(multipleValuesObjectsObjects(value)).map(item -> this.fields.get(slugs[0]).convert(item)).toArray(Object[]::new);
        } else {
            newValue = this.fields.get(slugs[0]).convert(value);
        }
        if (slugs.length == 1) {
            builder.equal(name, newValue);
            return;
        }
        MatchType.get(slugs[1]).build(this.builder, slugs[0], newValue);
    }

    public PropertyFilterBuilder getBuilder() {
        return this.builder;
    }

    public List<PropertyFilter> build() {
        return this.builder.build();
    }
}
