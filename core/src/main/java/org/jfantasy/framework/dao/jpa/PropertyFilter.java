package org.jfantasy.framework.dao.jpa;

import static org.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

import java.lang.reflect.Array;
import org.apache.commons.lang3.StringUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

/**
 * 通用过滤器
 *
 * @author limaofeng
 */
public class PropertyFilter {

  /** 完整表达式 */
  private String key;
  /** 名称 */
  private String propertyName;
  /** 值 */
  private Object propertyValue;
  /** 过滤类型 */
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
    return clazz.cast(this.propertyValue);
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
    /** 添加 and 链接符 */
    AND(
        "and",
        (builder, name, value) ->
            builder.and((PropertyFilterBuilder[]) multipleValuesObjectsObjects(value))),
    /** 添加 or 链接符 */
    OR(
        "or",
        (builder, name, value) ->
            builder.or((PropertyFilterBuilder[]) multipleValuesObjectsObjects(value))),
    /** 不等于 */
    NOT(
        "not",
        (builder, name, value) ->
            builder.not((PropertyFilterBuilder[]) multipleValuesObjectsObjects(value))),
    /** 等于 */
    EQ("equal", (builder, name, value) -> builder.equal(name, value)),
    /** 不等于 */
    NOT_EQUAL("notEqual", (builder, name, value) -> builder.notEqual(name, value)),

    CONTAINS("contains", (builder, name, value) -> builder.contains(name, (String) value)),

    NOT_CONTAINS(
        "notContains", (builder, name, value) -> builder.notContains(name, (String) value)),

    STARTS_WITH("startsWith", (builder, name, value) -> builder.startsWith(name, (String) value)),

    NOT_STARTS_WITH(
        "notStartsWith", (builder, name, value) -> builder.notStartsWith(name, (String) value)),

    ENDS_WITH("endsWith", (builder, name, value) -> builder.endsWith(name, (String) value)),

    NOT_ENDS_WITH(
        "notEndsWith", (builder, name, value) -> builder.notEndsWith(name, (String) value)),
    /** 小于 */
    LT("lt", (builder, name, value) -> builder.lessThan(name, value)),
    /** 大于 */
    GT("gt", (builder, name, value) -> builder.greaterThan(name, value)),
    /** 小于等于 */
    LTE("lte", (builder, name, value) -> builder.lessThanOrEqual(name, value)),
    /** 大于等于 */
    GTE("gte", (builder, name, value) -> builder.lessThanOrEqual(name, value)),
    /** in */
    IN("in", (builder, name, value) -> builder.in(name, multipleValuesObjectsObjects(value))),
    /** not in */
    NOT_IN("notIn", (builder, name, value) -> builder.notIn(name, value)),
    /** is null */
    NULL("null", (builder, name, value) -> builder.isNull(name)),
    /** not null */
    NOT_NULL("notNull", (builder, name, value) -> builder.isNotNull(name)),
    /** */
    EMPTY("empty", (builder, name, value) -> builder.isEmpty(name)),
    /** */
    NOT_EMPTY("notEmpty", (builder, name, value) -> builder.isNotEmpty(name)),

    BETWEEN(
        "between",
        (builder, name, value) -> {
          Comparable x = (Comparable) Array.get(value, 0);
          Comparable y = (Comparable) Array.get(value, 1);
          return builder.between(name, x, y);
        }),
    /** 模糊查询 */
    @Deprecated
    LIKE("like", (builder, name, value) -> builder.contains(name, (String) value)),
    /** 不存在 */
    @Deprecated
    NOTEMPTY("notEmpty", (builder, name, value) -> builder.isNotEmpty(name)),
    @Deprecated
    NOTNULL("notNull", (builder, name, value) -> builder.isNotNull(name)),
    @Deprecated
    NE("NE", (builder, name, value) -> builder.notEqual(name, value)),
    @Deprecated
    NOTIN("notIn", (builder, name, value) -> builder.notIn(name, (Object[]) value)),
    @Deprecated
    LE("lte", (builder, name, value) -> builder.lessThanOrEqual(name, value)),
    @Deprecated
    GE("gte", (builder, name, value) -> builder.greaterThanOrEqual(name, value));

    private final String slug;
    private final MatchBuilder builder;

    MatchType(String slug, MatchBuilder builder) {
      this.slug = slug;
      this.builder = builder;
    }

    public String getSlug() {
      return slug;
    }

    public static MatchType get(String str) {
      str = ObjectUtil.exists(new String[] {"AND", "OR", "NOT"}, str) ? str.toLowerCase() : str;
      for (MatchType matchType : MatchType.values()) {
        if (RegexpUtil.find(str, "^" + matchType.slug)) {
          return matchType;
        }
      }
      return null;
    }

    public static boolean is(String str) {
      return get(str) != null;
    }

    public <T> void build(PropertyFilterBuilder builder, String name, Object value) {
      this.builder.exec(builder, name, value);
    }
  }

  @Override
  public String toString() {
    return "PropertyFilter [matchType="
        + matchType
        + ", propertyName="
        + propertyName
        + ", propertyValue="
        + propertyValue
        + "]";
  }

  static interface MatchBuilder {
    PropertyFilterBuilder exec(PropertyFilterBuilder builder, String name, Object value);
  }
}
