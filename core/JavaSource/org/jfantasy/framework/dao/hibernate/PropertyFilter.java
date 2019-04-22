package org.jfantasy.framework.dao.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.dao.jpa.PropertyFilterBuilder;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 通用过滤器
 *
 * @author limaofeng
 */
public class PropertyFilter {

    private static final String OR_SEPARATOR = "_OR_";

    /**
     * 名称
     */
    private String[] propertyNames;
    /**
     * 类型
     */
    private Class<?> propertyType;
    /**
     * 值
     */
    private Object propertyValue;
    /**
     * 过滤类型
     */
    private MatchType matchType;
    /**
     * 完整表达式
     */
    private String filterName;

    public PropertyFilter(String filterName) {
        this.filterName = filterName;
        String matchTypeCode = StringUtils.substringBefore(filterName, "_");
        try {
            this.matchType = Enum.valueOf(MatchType.class, matchTypeCode);
        } catch (IgnoreException e) {
            throw new IllegalArgumentException(String.format("filter名称 %s 没有按规则编写,无法得到属性比较类型.", filterName), e);
        }
        String propertyNameStr = StringUtils.substringAfter(filterName, "_");
        this.propertyNames = propertyNameStr.split(OR_SEPARATOR);
    }

    public PropertyFilter(String filterName, Enum<?> value) {
        this.initialize(filterName);
        this.propertyValue = value;
    }

    public PropertyFilter(String filterName, Enum<?>... value) {
        this.initialize(filterName);
        if (!(MatchType.IN.equals(this.matchType) || MatchType.NOTIN.equals(this.matchType))) {
            throw new IgnoreException("有多个条件时,查询条件必须为 in 或者 not in ");
        }
        this.propertyValue = value;
    }

    public PropertyFilter(String filterName, String value) {
        this.initialize(filterName);
        this.setPropertyValue(value);
    }

    public <T> PropertyFilter(String filterName, T... value) {
        this.initialize(filterName);
        boolean multiple = Arrays.stream(new MatchType[]{MatchType.IN, MatchType.NOTIN}).anyMatch(type -> type == this.matchType);
        if (!multiple && value.length > 1) {
            throw new IgnoreException("有多个条件时,查询条件必须为 in 或者 not in ");
        }
        if (MatchType.BETWEEN == this.matchType) {
            Object array = ClassUtil.newInstance(this.propertyType, 2);
            Array.set(array, 0, Array.get(value, 0));
            Array.set(array, 1, Array.get(value, 1));
            this.propertyValue = array;
        } else if (multiple) {
            Object array = this.propertyType.isAssignableFrom(Enum.class) ? new String[value.length] : ClassUtil.newInstance(this.propertyType, Array.getLength(value));
            for (int i = 0; i < Array.getLength(value); i++) {
                Array.set(array, i, this.propertyType == Enum.class ? Array.get(value, i) : ReflectionUtils.convertStringToObject(Array.get(value, i).toString(), this.propertyType));
            }
            this.propertyValue = array;
        } else {
            setPropertyValue(value[0]);
        }
    }

    public static PropertyFilterBuilder builder() {
        return new PropertyFilterBuilder();
    }

    public static PropertyFilterBuilder builder(Class<?> entityClass) {
        return new PropertyFilterBuilder(entityClass);
    }

    private void setPropertyValue(String value) {
        if (MatchType.BETWEEN.equals(this.matchType)) {
            Object array = ClassUtil.newInstance(this.propertyType, 2);
            String[] tempArray = StringUtil.tokenizeToStringArray(value, "~");
            for (int i = 0; i < tempArray.length; i++) {
                Array.set(array, i, ReflectionUtils.convertStringToObject(tempArray[i], this.propertyType));
            }
            this.propertyValue = array;
        } else if (this.propertyType == Enum.class) {
            this.propertyValue = value;
        } else {
            this.propertyValue = ReflectionUtils.convertStringToObject(value, this.propertyType);
        }
    }

    private void initialize(String filterName) {
        String errorTemplate = "filter名称 %s 没有按规则编写,无法得到属性比较类型.";
        this.filterName = filterName;
        String matchTypeStr = StringUtils.substringBefore(filterName, "_");
        this.matchType = MatchType.get(matchTypeStr);
        Assert.notNull(this.matchType, String.format(errorTemplate, filterName));
        this.propertyType = PropertyType.S.getValue();
        String propertyNameStr = StringUtils.substringAfter(filterName, "_");
        this.propertyNames = propertyNameStr.split(OR_SEPARATOR);
        Assert.isTrue(this.propertyNames.length > 0, String.format(errorTemplate, filterName));
    }

    public boolean isMultiProperty() {
        return this.propertyNames.length > 1;
    }

    public String[] getPropertyNames() {
        return this.propertyNames;
    }

    public String getPropertyName() {
        if (this.propertyNames.length > 1) {
            throw new IllegalArgumentException("There are not only one property");
        }
        return this.propertyNames[0];
    }

    public Object getPropertyValue() {
        return this.propertyValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(Class<T> clazz) {
        if (clazz.isInstance(this.getPropertyValue())) {
            return clazz.cast(this.propertyValue);
        }
        boolean multiple = Arrays.stream(new MatchType[]{MatchType.IN, MatchType.NOTIN, MatchType.BETWEEN}).anyMatch(type -> type == this.matchType);
        if (multiple) {
            clazz = clazz.isArray() ? clazz : (Class<T>) ClassUtil.newInstance(clazz, 0).getClass();
            Class componentType = clazz.getComponentType();
            Object array = ClassUtil.newInstance(clazz.isArray() ? clazz.getComponentType() : clazz, Array.getLength(propertyValue));
            for (int i = 0; i < Array.getLength(propertyValue); i++) {
                Object value = Array.get(propertyValue, i);
                if (!componentType.isInstance(value)) {
                    if (clazz.getComponentType().isEnum()) {
                        AtomicReference<Class> enumClass = new AtomicReference<>(componentType);
                        value = Enum.valueOf(enumClass.get(), (String) value);
                    } else {
                        value = ReflectionUtils.convert(Array.get(propertyValue, i), componentType);
                    }
                }
                Array.set(array, i, value);
            }
            return clazz.cast(array);
        }
        return ReflectionUtils.convert(this.getPropertyValue(), clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getPropertyType() {
        return (Class<T>) this.propertyType;
    }

    public MatchType getMatchType() {
        return this.matchType;
    }

    public void setPropertyValue(Object propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public enum MatchType {
        /**
         * 等于
         */
        EQ(false),
        /**
         * 模糊查询
         */
        LIKE(false),
        /**
         * 小于
         */
        LT(false),
        /**
         * 大于
         */
        GT(false),
        /**
         * 小于等于
         */
        LE(false),
        /**
         * 大于等于
         */
        GE(false),
        /**
         * in
         */
        IN(true),
        /**
         * not in
         */
        NOTIN(true),
        /**
         * 不等于
         */
        NE(false),
        /**
         * is null
         */
        NULL,
        /**
         * not null
         */
        NOTNULL,
        /**
         *
         */
        EMPTY,
        /**
         *
         */
        NOTEMPTY, BETWEEN(false), SQL(false);

        /**
         * 是否存在参数
         */
        private boolean none;
        /**
         * 是否有多个参数
         */
        private boolean multi;

        MatchType() {
            this(true, false);
        }

        MatchType(boolean multi) {
            this(false, multi);
        }

        MatchType(boolean none, boolean multi) {
            this.none = none;
            this.multi = multi;
        }

        public static MatchType get(String str) {
            for (MatchType matchType : MatchType.values()) {
                if (RegexpUtil.find(str, "^" + matchType.toString())) {
                    return matchType;
                }
            }
            return null;
        }

        public static boolean is(String str) {
            return get(str) != null;
        }

        public boolean isMulti() {
            return multi;
        }

        public boolean isNone() {
            return none;
        }
    }

    public enum PropertyType {
        S(String.class), I(Integer.class), L(Long.class), N(Double.class), D(Date.class), B(Boolean.class), M(BigDecimal.class), E(Enum.class);

        private Class clazz;

        PropertyType(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class getValue() {
            return this.clazz;
        }

    }

    public String getFilterName() {
        return filterName;
    }

    @Override
    public String toString() {
        return "PropertyFilter [matchType=" + matchType + ", propertyNames=" + Arrays.toString(propertyNames) + ", propertyType=" + propertyType + ", propertyValue=" + propertyValue + "]";
    }

}