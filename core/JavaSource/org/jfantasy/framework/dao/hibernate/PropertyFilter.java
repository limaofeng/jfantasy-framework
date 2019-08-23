package org.jfantasy.framework.dao.hibernate;

import lombok.Data;
import org.jfantasy.framework.dao.jpa.PropertyFilterBuilder;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

/**
 * 通用过滤器
 *
 * @author limaofeng
 */
@Data
public class PropertyFilter {

    /**
     * 完整表达式
     */
    private String key;
    /**
     * 名称
     */
    private String propertyName;
    /**
     * 值
     */
    private Object propertyValue;
    /**
     * 过滤类型
     */
    private MatchType matchType;

    public <T> PropertyFilter(MatchType matchType, T value) {
        this.matchType = matchType;
        this.propertyValue = value;
    }

    public <T> PropertyFilter(MatchType matchType, String propertyName) {
        this.initialize(matchType, propertyName);
    }

    public <T> PropertyFilter(MatchType matchType, String propertyName, T value) {
        this.initialize(matchType, propertyName);
        this.setPropertyValue(value);
    }

    public <T> PropertyFilter(MatchType matchType, String propertyName, T... value) {
        this.initialize(matchType, propertyName);
        this.setPropertyValue(value);
    }

    public static PropertyFilterBuilder builder() {
        return new PropertyFilterBuilder();
    }

    private <T> void setPropertyValue(T... value) {
        boolean multiple = Arrays.stream(new MatchType[]{MatchType.IN, MatchType.NOTIN}).anyMatch(type -> type == this.matchType);
        if (!multiple && value.length > 1) {
            throw new IgnoreException("有多个条件时,查询条件必须为 in 或者 not in ");
        }
        this.propertyValue = value;
    }

    private void initialize(MatchType matchType, String propertyName) {
        this.matchType = matchType;
        this.propertyName = propertyName;
    }

    public String getKey() {
        return this.matchType + "_" + propertyName;
    }


    public String getPropertyName() {
        return this.propertyName;
    }

    public <T> T getPropertyValue() {
        return (T) this.propertyValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(Class<T> clazz) {
        return (T) this.propertyValue;
    }

    public String getFilterName() {
        return this.matchType + "_" + this.propertyName;
    }

    public boolean isPropertyFilter() {
        return ClassUtil.isList(this.propertyValue);
    }

    public boolean isSpecification() {
        return this.propertyValue instanceof Specification;
    }

    public boolean isExpression() {
        return isPropertyFilter() || isSpecification();
    }

    public enum MatchType {
        /**
         * 添加 and 链接符
         */
        AND(false),
        /**
         * 添加 or 链接符
         */
        OR(false),
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

    @Override
    public String toString() {
        return "PropertyFilter [matchType=" + matchType + ", propertyName=" + propertyName + ", propertyValue=" + propertyValue + "]";
    }

}