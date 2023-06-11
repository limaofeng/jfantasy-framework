package org.jfantasy.framework.dao.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.jfantasy.framework.dao.MatchType;

/**
 * 属性过滤器 构造器
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-22 17:55
 */
public abstract class PropertyFilterBuilder<P extends PropertyFilter, C> implements PropertyFilter {

  protected C context;
  protected final Map<String, PropertyDefinition<C>> properties = new HashMap<>();
  protected final Map<String, JunctionPredicateCallback<C>> junctions = new HashMap<>();

  protected void property(PropertyPredicateCallback<C> property) {
    PropertyDefinition<C> definition = PropertyDefinition.<C>builder().name("*").build();
    definition.getPredicates().put("*", property);
    properties.put("*", definition);
  }

  protected void junction(MatchType matchType, JunctionPredicateCallback<C> junction) {
    junctions.put(matchType.name(), junction);
  }

  protected void property(String name, PropertyPredicateCallback<C> callback) {
    PropertyDefinition<C> definition = PropertyDefinition.<C>builder().name(name).build();
    definition.getPredicates().put("*", callback);
    properties.put(name, definition);
  }

  protected void property(
      String name, MatchType[] matchTypes, PropertyPredicateCallback<C> callback) {
    PropertyDefinition<C> definition = PropertyDefinition.<C>builder().name(name).build();
    for (MatchType matchType : matchTypes) {
      definition.getPredicates().put(matchType.name(), callback);
    }
    properties.put(name, definition);
  }

  protected PropertyPredicateCallback<C> predicate(String name, MatchType matchType) {
    PropertyDefinition<C> definition = this.properties.get(name);
    if (definition == null) {
      definition = this.properties.get("*");
    }
    if (definition == null) {
      throw new PropertyNotFoundException(name);
    }
    PropertyPredicateCallback<C> predicate = definition.getPredicates().get(matchType.name());
    if (predicate == null) {
      predicate = definition.getPredicates().get("*");
    }
    if (predicate == null) {
      throw new PropertyNotFoundException(name, "未匹配到[" + matchType.name() + "]对应的过滤条件");
    }
    return predicate;
  }

  private JunctionPredicateCallback<C> junction(MatchType matchType) {
    return this.junctions.get(matchType.name());
  }

  protected PropertyFilterBuilder(C context) {
    this.context = context;
  }

  @Override
  public abstract <T> T build();

  /**
   * 等于
   *
   * @param name 字段名
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilterBuilder
   */
  @Override
  public <T> P equal(String name, T value) {
    this.predicate(name, MatchType.EQ).apply(name, MatchType.EQ, value, context);
    return (P) this;
  }

  /**
   * 模糊查询
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilterBuilder
   */
  @Override
  public P contains(String name, String value) {
    this.predicate(name, MatchType.CONTAINS).apply(name, MatchType.CONTAINS, value, context);
    return (P) this;
  }

  @Override
  public P notContains(String name, String value) {
    this.predicate(name, MatchType.NOT_CONTAINS)
        .apply(name, MatchType.NOT_CONTAINS, value, context);
    return (P) this;
  }

  @Override
  public P startsWith(String name, String value) {
    this.predicate(name, MatchType.STARTS_WITH)
        .apply(name, MatchType.STARTS_WITH, value + "%", context);
    return (P) this;
  }

  @Override
  public P notStartsWith(String name, String value) {
    this.predicate(name, MatchType.NOT_STARTS_WITH)
        .apply(name, MatchType.NOT_STARTS_WITH, value + "%", context);
    return (P) this;
  }

  @Override
  public P endsWith(String name, String value) {
    this.predicate(name, MatchType.ENDS_WITH)
        .apply(name, MatchType.ENDS_WITH, "%" + value, context);
    return (P) this;
  }

  @Override
  public P notEndsWith(String name, String value) {
    this.predicate(name, MatchType.NOT_ENDS_WITH)
        .apply(name, MatchType.NOT_ENDS_WITH, "%" + value, context);
    return (P) this;
  }

  /** 小于 */
  @Override
  public <T> P lessThan(String name, T value) {
    this.predicate(name, MatchType.LT).apply(name, MatchType.LT, value, context);
    return (P) this;
  }

  /** 大于 */
  @Override
  public P greaterThan(String name, Object value) {
    this.predicate(name, MatchType.GT).apply(name, MatchType.GT, value, context);
    return (P) this;
  }

  /** 小于等于 */
  @Override
  public P lessThanOrEqual(String name, Object value) {
    this.predicate(name, MatchType.LTE).apply(name, MatchType.LTE, value, context);
    return (P) this;
  }

  /** 大于等于 */
  @Override
  public P greaterThanOrEqual(String name, Object value) {
    this.predicate(name, MatchType.GTE).apply(name, MatchType.GTE, value, context);
    return (P) this;
  }

  /** in */
  @Override
  public final <T> P in(String name, T... value) {
    this.predicate(name, MatchType.IN).apply(name, MatchType.IN, value, context);
    return (P) this;
  }

  /**
   * @param name 名称
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  @Override
  public <T> P in(String name, List<T> value) {
    this.predicate(name, MatchType.IN).apply(name, MatchType.IN, value, context);
    return (P) this;
  }

  /**
   * not in
   *
   * @param name 名称
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  @Override
  public final <T> P notIn(String name, T... value) {
    this.predicate(name, MatchType.NOT_IN).apply(name, MatchType.NOT_IN, value, context);
    return (P) this;
  }

  /** 不等于 */
  @Override
  public <T> P notEqual(String name, T value) {
    this.predicate(name, MatchType.NOT_EQUAL).apply(name, MatchType.NOT_EQUAL, value, context);
    return (P) this;
  }

  /** is null */
  @Override
  public P isNull(String name) {
    this.predicate(name, MatchType.NULL).apply(name, MatchType.NULL, null, context);
    return (P) this;
  }

  /** not null */
  @Override
  public P isNotNull(String name) {
    this.predicate(name, MatchType.NOT_NULL).apply(name, MatchType.NOT_NULL, null, context);
    return (P) this;
  }

  /** */
  @Override
  public P isEmpty(String name) {
    this.predicate(name, MatchType.EMPTY).apply(name, MatchType.EMPTY, null, context);
    return (P) this;
  }

  /** */
  @Override
  public P isNotEmpty(String name) {
    this.predicate(name, MatchType.NOT_EMPTY).apply(name, MatchType.NOT_EMPTY, null, context);
    return (P) this;
  }

  @Override
  public <Y extends Comparable<? super Y>> P between(String name, Y x, Y y) {
    this.predicate(name, MatchType.BETWEEN)
        .apply(name, MatchType.BETWEEN, new BetweenValue(x, y), context);
    return (P) this;
  }

  @Override
  public P and(PropertyFilter... filters) {
    this.junction(MatchType.AND).apply(context, MatchType.AND, filters);
    return (P) this;
  }

  @Override
  public P or(PropertyFilter... filters) {
    this.junction(MatchType.OR).apply(context, MatchType.OR, filters);
    return (P) this;
  }

  @Override
  public P not(PropertyFilter... filters) {
    this.junction(MatchType.NOT).apply(context, MatchType.NOT, filters);
    return (P) this;
  }

  @Data
  @Builder
  private static class PropertyDefinition<C> {
    private String name;
    @Builder.Default private Map<String, PropertyPredicateCallback<C>> predicates = new HashMap<>();
  }

  protected interface PropertyPredicateCallback<C> {
    /**
     * 应用
     *
     * @param name 名称
     * @param matchType 匹配类型
     * @param value 值
     * @param context 上下文
     */
    void apply(String name, MatchType matchType, Object value, C context);
  }

  interface JunctionPredicateCallback<C> {
    /**
     * 应用
     *
     * @param context 上下文
     * @param matchType 匹配类型
     * @param filters 过滤器
     */
    void apply(C context, MatchType matchType, PropertyFilter... filters);
  }
}
