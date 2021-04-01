package org.jfantasy.framework.dao.jpa;

import org.apache.commons.lang3.StringUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

/**
 * 通用过滤器
 *
 * @author limaofeng
 */
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

    @Deprecated
    public <T> PropertyFilter(String filterName, T value) {
        String errorTemplate = "filter名称 %s 没有按规则编写,无法得到属性比较类型.";
        String matchTypeStr = StringUtils.substringBefore(filterName, "_");
        this.matchType = MatchType.get(matchTypeStr);
        Assert.notNull(this.matchType, String.format(errorTemplate, filterName));
        this.propertyName = StringUtils.substringAfter(filterName, "_");
        this.propertyValue = value;
    }

    @Deprecated
    public <T> PropertyFilter(String filterName) {
        String errorTemplate = "filter名称 %s 没有按规则编写,无法得到属性比较类型.";
        String matchTypeStr = StringUtils.substringBefore(filterName, "_");
        this.matchType = MatchType.get(matchTypeStr);
        Assert.notNull(this.matchType, String.format(errorTemplate, filterName));
        this.propertyName = StringUtils.substringAfter(filterName, "_");
    }

    @Deprecated
    public <T> PropertyFilter(String filterName, T... value) {
        String errorTemplate = "filter名称 %s 没有按规则编写,无法得到属性比较类型.";
        String matchTypeStr = StringUtils.substringBefore(filterName, "_");
        this.matchType = MatchType.get(matchTypeStr);
        Assert.notNull(this.matchType, String.format(errorTemplate, filterName));
        this.propertyName = StringUtils.substringAfter(filterName, "_");
        this.propertyValue = value;
    }

    public <T> PropertyFilter(MatchType matchType, String propertyName) {
        this.initialize(matchType, propertyName);
    }

    public <T> PropertyFilter(MatchType matchType, String propertyName, T value) {
        this.initialize(matchType, propertyName);
        this.propertyValue = value;
    }

    public <T> PropertyFilter(MatchType matchType, String propertyName, T... value) {
        this.initialize(matchType, propertyName);
        this.propertyValue = value;
    }

    public static PropertyFilterBuilder builder() {
        return new PropertyFilterBuilder();
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

    public MatchType getMatchType() {
        return this.matchType;
    }

    public enum MatchType {
        /**
         * 添加 and 链接符
         */
        AND("and"),
        /**
         * 添加 or 链接符
         */
        OR("or"),
        /**
         * 等于
         */
        EQ("equal"),
        /**
         * 不等于
         */
        NOT("not"),

        CONTAINS("contains"),

        NOT_CONTAINS("notContains"),

        STARTS_WITH("startsWith"),

        NOT_STARTS_WITH("notStartsWith"),

        ENDS_WITH("endsWith"),

        NOT_ENDS_WITH("notEndsWith"),
        /**
         * 小于
         */
        LT("lt"),
        /**
         * 大于
         */
        GT("gt"),
        /**
         * 小于等于
         */
        LTE("lte"),
        /**
         * 大于等于
         */
        GTE("gte"),
        /**
         * in
         */
        IN("in"),
        /**
         * not in
         */
        NOT_IN("notIn"),
        /**
         * 不等于
         */
        NOT_EQUAL("notEqual"),
        /**
         * is null
         */
        NULL("null"),
        /**
         * not null
         */
        NOT_NULL("notNull"),
        /**
         *
         */
        EMPTY("empty"),
        /**
         *
         */
        NOT_EMPTY("notEmpty"),

        BETWEEN("between"),
        /**
         * 模糊查询
         */
        @Deprecated
        LIKE("like"),
        /**
         * 不存在
         */
        @Deprecated
        NOTEMPTY("notEmpty"),
        @Deprecated
        NOTNULL("notNull"),
        @Deprecated
        NE("NE"),
        @Deprecated
        NOTIN("notIn"),
        @Deprecated
        LE("lte"),
        @Deprecated
        GE("gte");

        private final String slug;

        MatchType(String slug) {
            this.slug = slug;
        }

        public String getSlug() {
            return slug;
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

    }

    @Override
    public String toString() {
        return "PropertyFilter [matchType=" + matchType + ", propertyName=" + propertyName + ", propertyValue=" + propertyValue + "]";
    }

}