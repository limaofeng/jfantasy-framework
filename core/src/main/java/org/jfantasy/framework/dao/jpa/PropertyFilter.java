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

    protected <T> PropertyFilter(MatchType matchType, String propertyName) {
        this.initialize(matchType, propertyName);
    }

    protected <T> PropertyFilter(MatchType matchType, String propertyName, T value) {
        this.initialize(matchType, propertyName);
        this.propertyValue = value;
    }

    protected <T> PropertyFilter(MatchType matchType, String propertyName, T... value) {
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