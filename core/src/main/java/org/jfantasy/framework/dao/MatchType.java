package org.jfantasy.framework.dao;

import static org.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

import java.lang.reflect.Array;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * 匹配类型
 *
 * @author limaofeng
 */
public enum MatchType {
  /** 添加 and 链接符 */
  AND("and", (builder, name, value) -> builder.and(multipleValuesObjectsObjects(value))),
  /** 添加 or 链接符 */
  OR("or", (builder, name, value) -> builder.or(multipleValuesObjectsObjects(value))),
  /** 不等于 */
  NOT("not", (builder, name, value) -> builder.not(multipleValuesObjectsObjects(value))),
  /** 等于 */
  EQ("equal", PropertyFilter::equal),
  /** 不等于 */
  NOT_EQUAL("notEqual", PropertyFilter::notEqual),

  CONTAINS("contains", (builder, name, value) -> builder.contains(name, (String) value)),

  NOT_CONTAINS("notContains", (builder, name, value) -> builder.notContains(name, (String) value)),

  STARTS_WITH("startsWith", (builder, name, value) -> builder.startsWith(name, (String) value)),

  NOT_STARTS_WITH(
      "notStartsWith", (builder, name, value) -> builder.notStartsWith(name, (String) value)),

  ENDS_WITH("endsWith", (builder, name, value) -> builder.endsWith(name, (String) value)),

  NOT_ENDS_WITH("notEndsWith", (builder, name, value) -> builder.notEndsWith(name, (String) value)),
  /** 小于 */
  LT("lt", PropertyFilter::lessThan),
  /** 大于 */
  GT("gt", PropertyFilter::greaterThan),
  /** 小于等于 */
  LTE("lte", PropertyFilter::lessThanOrEqual),
  /** 大于等于 */
  GTE("gte", PropertyFilter::lessThanOrEqual),
  /** in */
  IN("in", (builder, name, value) -> builder.in(name, multipleValuesObjectsObjects(value))),
  /** not in */
  NOT_IN(
      "notIn", (builder, name, value) -> builder.notIn(name, multipleValuesObjectsObjects(value))),
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
      });

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
      if (matchType.slug.equals(str)) {
        return matchType;
      }
    }
    return null;
  }

  public static boolean is(String str) {
    return get(str) != null;
  }

  public <T> void build(PropertyFilter builder, String name, Object value) {
    this.builder.exec(builder, name, value);
  }

  interface MatchBuilder {
    /**
     * 生成 PropertyFilter
     *
     * @param builder
     * @param name
     * @param value
     * @return
     */
    PropertyFilter exec(PropertyFilter builder, String name, Object value);
  }
}
