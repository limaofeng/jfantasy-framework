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
public abstract class PropertyFilterBuilder<C> implements PropertyFilter {

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
    return this.junctions.get(matchType);
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
  public <T> PropertyFilter equal(String name, T value) {
    this.predicate(name, MatchType.CONTAINS).apply(name, MatchType.EQ, value, context);
    return this;
  }

  /**
   * 模糊查询
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilterBuilder
   */
  @Override
  public PropertyFilter contains(String name, String value) {
    this.predicate(name, MatchType.CONTAINS).apply(name, MatchType.CONTAINS, value, context);
    return this;
  }

  @Override
  public PropertyFilter notContains(String name, String value) {
    this.predicate(name, MatchType.NOT_CONTAINS)
        .apply(name, MatchType.NOT_CONTAINS, value, context);
    return this;
  }

  @Override
  public PropertyFilter startsWith(String name, String value) {
    this.predicate(name, MatchType.STARTS_WITH)
        .apply(name, MatchType.STARTS_WITH, value + "%", context);
    return this;
  }

  @Override
  public PropertyFilter notStartsWith(String name, String value) {
    this.predicate(name, MatchType.NOT_STARTS_WITH)
        .apply(name, MatchType.NOT_STARTS_WITH, value + "%", context);
    return this;
  }

  @Override
  public PropertyFilter endsWith(String name, String value) {
    this.predicate(name, MatchType.ENDS_WITH)
        .apply(name, MatchType.ENDS_WITH, "%" + value, context);
    return this;
  }

  @Override
  public PropertyFilter notEndsWith(String name, String value) {
    this.predicate(name, MatchType.NOT_ENDS_WITH)
        .apply(name, MatchType.NOT_ENDS_WITH, "%" + value, context);
    return this;
  }

  /** 小于 */
  @Override
  public <T> PropertyFilter lessThan(String name, T value) {
    this.predicate(name, MatchType.LT).apply(name, MatchType.LT, value, context);
    return this;
  }

  /** 大于 */
  @Override
  public PropertyFilter greaterThan(String name, Object value) {
    this.predicate(name, MatchType.GT).apply(name, MatchType.GT, value, context);
    return this;
  }

  /** 小于等于 */
  @Override
  public PropertyFilter lessThanOrEqual(String name, Object value) {
    this.predicate(name, MatchType.LTE).apply(name, MatchType.LTE, value, context);
    return this;
  }

  /** 大于等于 */
  @Override
  public PropertyFilter greaterThanOrEqual(String name, Object value) {
    this.predicate(name, MatchType.GTE).apply(name, MatchType.GTE, value, context);
    return this;
  }

  /** in */
  @Override
  public final <T> PropertyFilter in(String name, T... value) {
    this.predicate(name, MatchType.IN).apply(name, MatchType.IN, value, context);
    return this;
  }

  /**
   * @param name 名称
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  @Override
  public <T> PropertyFilter in(String name, List<T> value) {
    this.predicate(name, MatchType.IN).apply(name, MatchType.IN, value, context);
    return this;
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
  @SafeVarargs
  public final <T> PropertyFilter notIn(String name, T... value) {
    this.predicate(name, MatchType.NOT_IN).apply(name, MatchType.NOT_IN, value, context);
    return this;
  }

  /** 不等于 */
  @Override
  public <T> PropertyFilter notEqual(String name, T value) {
    this.predicate(name, MatchType.NOT_EQUAL).apply(name, MatchType.NOT_EQUAL, value, context);
    return this;
  }

  /** is null */
  @Override
  public PropertyFilter isNull(String name) {
    this.predicate(name, MatchType.NULL).apply(name, MatchType.NULL, null, context);
    return this;
  }

  /** not null */
  @Override
  public PropertyFilter isNotNull(String name) {
    this.predicate(name, MatchType.NOT_NULL).apply(name, MatchType.NOT_NULL, null, context);
    return this;
  }

  /** */
  @Override
  public PropertyFilter isEmpty(String name) {
    this.predicate(name, MatchType.EMPTY).apply(name, MatchType.EMPTY, null, context);
    return this;
  }

  /** */
  @Override
  public PropertyFilter isNotEmpty(String name) {
    this.predicate(name, MatchType.NOT_EMPTY).apply(name, MatchType.NOT_EMPTY, null, context);
    return this;
  }

  @Override
  public <Y extends Comparable<? super Y>> PropertyFilter between(String name, Y x, Y y) {
    this.predicate(name, MatchType.BETWEEN)
        .apply(name, MatchType.BETWEEN, new BetweenValue(x, y), context);
    return this;
  }

  @Override
  public PropertyFilter and(PropertyFilter... filters) {
    this.junction(MatchType.AND).apply(context, MatchType.AND, filters);
    return this;
  }

  //    @SafeVarargs
  //    public final PropertyFilter and(List<PropertyPredicate>... filters) {
  ////        this.filters.add(new PropertyPredicate(MatchType.AND, Arrays.asList(filters)));
  //        return this;
  //    }

  //    public PropertyFilter and(Specification... specifications) {
  ////        this.filters.add(new PropertyPredicate(MatchType.AND, Arrays.asList(specifications)));
  //        return this;
  //    }

  //    @SafeVarargs
  //    public final PropertyFilter or(List<PropertyPredicate>... filters) {
  ////        this.filters.add(new PropertyPredicate(MatchType.OR, Arrays.asList(filters)));
  //        return this;
  //    }

  @Override
  public PropertyFilter or(PropertyFilter... filters) {
    this.junction(MatchType.OR).apply(context, MatchType.OR, filters);
    return this;
  }

  //    public PropertyFilter or(Specification... specifications) {
  //        this.filters.add(new PropertyPredicate(MatchType.OR, specifications));
  //        return this;
  //    }

  //    @SafeVarargs
  //    public final PropertyFilter not(List<PropertyPredicate>... filters) {
  //        this.filters.add(new PropertyPredicate(MatchType.NOT, Arrays.asList(filters)));
  //        return this;
  //    }

  @Override
  public PropertyFilter not(PropertyFilter... filters) {
    this.junction(MatchType.NOT).apply(context, MatchType.NOT, filters);
    return this;
  }

  //    public PropertyFilter not(Specification... specifications) {
  //        this.filters.add(new PropertyPredicate(MatchType.NOT, specifications));
  //        return this;
  //    }

  @Data
  @Builder
  private static class PropertyDefinition<C> {
    private String name;
    @Builder.Default private Map<String, PropertyPredicateCallback<C>> predicates = new HashMap<>();
  }

  interface PropertyPredicateCallback<C> {
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
