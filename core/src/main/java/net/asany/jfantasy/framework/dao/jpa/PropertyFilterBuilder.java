package net.asany.jfantasy.framework.dao.jpa;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import net.asany.jfantasy.framework.dao.MatchType;
import net.asany.jfantasy.framework.util.common.ClassUtil;

/**
 * 属性过滤器 构造器
 *
 * @author limaofeng
 * @version V1.0
 */
public abstract class PropertyFilterBuilder<P extends PropertyFilter, C> implements PropertyFilter {

  protected C context;
  protected Class<?> entityClass;
  protected final Map<String, PropertyDefinition<C>> properties = new HashMap<>();
  protected final Map<String, JunctionPredicateCallback<C>> junctions = new HashMap<>();
  protected static Map<Class<?>, Class<? extends PropertyFilter>> FILTERS = new HashMap<>();
  protected static final Map<Class<?>, Map<String, TypeConverter<?>>> CUSTOM_CONVERTERS =
      new HashMap<>();
  protected static final Map<Class<?>, Map<String, PropertyDefinition<?>>> CUSTOM_PROPERTIES =
      new HashMap<>();

  protected PropertyFilterBuilder(Class<?> entityClass, C context) {
    this.context = context;
    this.entityClass = entityClass;
  }

  protected PropertyFilterBuilder(C context) {
    this.context = context;
  }

  protected static Map<String, TypeConverter<?>> initDefaultConverters(Class<?> entityClass) {
    return PropertyFilterBuilder.CUSTOM_CONVERTERS.computeIfAbsent(
        entityClass,
        (clazz) -> {
          Map<String, TypeConverter<?>> fields = new HashMap<>();
          for (Field field : ClassUtil.getDeclaredFields(clazz)) {
            if (ClassUtil.isBasicType(field.getType())) {
              fields.put(field.getName(), new DefaultTypeConverter<>(field.getType()));
            }
          }
          return fields;
        });
  }

  protected void junction(MatchType matchType, JunctionPredicateCallback<C> junction) {
    junctions.put(matchType.name(), junction);
  }

  protected void property(PropertyPredicateCallback<C> property) {
    PropertyDefinition<C> definition = PropertyDefinition.<C>builder().name("*").build();
    definition.getPredicates().put("*", property);
    properties.put("*", definition);
  }

  protected void custom(String name, PropertyPredicateCallback<C> callback) {
    PropertyDefinition<C> definition = PropertyDefinition.<C>builder().name(name).build();
    definition.getPredicates().put("*", callback);
    properties.put(name, definition);
  }

  protected void custom(
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
    this.predicate(name, MatchType.EQ)
        .apply(name, MatchType.EQ, convert(name, MatchType.EQ, value), context);
    //noinspection unchecked
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
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P notContains(String name, String value) {
    this.predicate(name, MatchType.NOT_CONTAINS)
        .apply(name, MatchType.NOT_CONTAINS, value, context);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P startsWith(String name, String value) {
    this.predicate(name, MatchType.STARTS_WITH)
        .apply(name, MatchType.STARTS_WITH, value + "%", context);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P notStartsWith(String name, String value) {
    this.predicate(name, MatchType.NOT_STARTS_WITH)
        .apply(name, MatchType.NOT_STARTS_WITH, value + "%", context);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P endsWith(String name, String value) {
    this.predicate(name, MatchType.ENDS_WITH)
        .apply(name, MatchType.ENDS_WITH, "%" + value, context);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P notEndsWith(String name, String value) {
    this.predicate(name, MatchType.NOT_ENDS_WITH)
        .apply(name, MatchType.NOT_ENDS_WITH, "%" + value, context);
    //noinspection unchecked
    return (P) this;
  }

  /** 小于 */
  @Override
  public <T> P lessThan(String name, T value) {
    this.predicate(name, MatchType.LT)
        .apply(name, MatchType.LT, convert(name, MatchType.LT, value), context);
    //noinspection unchecked
    return (P) this;
  }

  /** 大于 */
  @Override
  public P greaterThan(String name, Object value) {
    this.predicate(name, MatchType.GT)
        .apply(name, MatchType.GT, convert(name, MatchType.GT, value), context);
    //noinspection unchecked
    return (P) this;
  }

  /** 小于等于 */
  @Override
  public P lessThanOrEqual(String name, Object value) {
    this.predicate(name, MatchType.LTE)
        .apply(name, MatchType.LTE, convert(name, MatchType.LTE, value), context);
    //noinspection unchecked
    return (P) this;
  }

  /** 大于等于 */
  @Override
  public P greaterThanOrEqual(String name, Object value) {
    this.predicate(name, MatchType.GTE)
        .apply(name, MatchType.GTE, convert(name, MatchType.GTE, value), context);
    //noinspection unchecked
    return (P) this;
  }

  /** in */
  @SafeVarargs
  @Override
  public final <T> P in(String name, T... value) {
    this.predicate(name, MatchType.IN)
        .apply(name, MatchType.IN, convert(name, MatchType.IN, value), context);
    //noinspection unchecked
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
    this.predicate(name, MatchType.IN)
        .apply(name, MatchType.IN, convert(name, MatchType.IN, value), context);
    //noinspection unchecked
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
  @SafeVarargs
  @Override
  public final <T> P notIn(String name, T... value) {
    this.predicate(name, MatchType.NOT_IN)
        .apply(name, MatchType.NOT_IN, convert(name, MatchType.NOT_IN, value), context);
    //noinspection unchecked
    return (P) this;
  }

  /** 不等于 */
  @Override
  public <T> P notEqual(String name, T value) {
    this.predicate(name, MatchType.NOT_EQUAL)
        .apply(name, MatchType.NOT_EQUAL, convert(name, MatchType.NOT_EQUAL, value), context);
    //noinspection unchecked
    return (P) this;
  }

  /** is null */
  @Override
  public P isNull(String name) {
    this.predicate(name, MatchType.NULL).apply(name, MatchType.NULL, null, context);
    //noinspection unchecked
    return (P) this;
  }

  /** not null */
  @Override
  public P isNotNull(String name) {
    this.predicate(name, MatchType.NOT_NULL).apply(name, MatchType.NOT_NULL, null, context);
    //noinspection unchecked
    return (P) this;
  }

  /** */
  @Override
  public P isEmpty(String name) {
    this.predicate(name, MatchType.EMPTY).apply(name, MatchType.EMPTY, null, context);
    //noinspection unchecked
    return (P) this;
  }

  /** */
  @Override
  public P isNotEmpty(String name) {
    this.predicate(name, MatchType.NOT_EMPTY).apply(name, MatchType.NOT_EMPTY, null, context);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public <Y extends Comparable<? super Y>> P between(String name, Y x, Y y) {
    this.predicate(name, MatchType.BETWEEN)
        .apply(name, MatchType.BETWEEN, new BetweenValue<>(x, y), context);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P and(PropertyFilter... filters) {
    this.junction(MatchType.AND).apply(context, MatchType.AND, filters);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P or(PropertyFilter... filters) {
    this.junction(MatchType.OR).apply(context, MatchType.OR, filters);
    //noinspection unchecked
    return (P) this;
  }

  @Override
  public P not(PropertyFilter... filters) {
    this.junction(MatchType.NOT).apply(context, MatchType.NOT, filters);
    //noinspection unchecked
    return (P) this;
  }

  public List<String> getPropertyNames() {
    return this.properties.keySet().stream()
        .filter(name -> !"*".equals(name))
        .collect(Collectors.toList());
  }

  public boolean hasProperty(String name) {
    return PropertyFilter.hasProperty(this.entityClass, name);
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

  /**
   * 复杂条件连接器
   *
   * @param <C>
   */
  protected interface JunctionPredicateCallback<C> {
    /**
     * 应用
     *
     * @param context 上下文
     * @param matchType 匹配类型
     * @param filters 过滤器
     */
    void apply(C context, MatchType matchType, PropertyFilter... filters);
  }

  protected <IV, OV> OV convert(String name, MatchType matchType, IV value) {
    if (entityClass == null || !CUSTOM_CONVERTERS.containsKey(entityClass)) {
      //noinspection unchecked
      return (OV) value;
    }
    Map<String, TypeConverter<?>> typeConverterMap = CUSTOM_CONVERTERS.get(entityClass);
    if (!typeConverterMap.containsKey(name)) {
      //noinspection unchecked
      return (OV) value;
    }
    TypeConverter<?> converter = typeConverterMap.get(name);
    if (MatchType.isMultipleValues(matchType)) {
      //noinspection unchecked
      return (OV)
          Arrays.stream(multipleValuesObjectsObjects(value))
              .map(converter::convert)
              .toArray(Object[]::new);
    }
    //noinspection unchecked
    return (OV) converter.convert(value);
  }

  public static <T> T[] multipleValuesObjectsObjects(Object value) {
    if (ClassUtil.isArray(value)) {
      //noinspection unchecked
      return (T[]) value;
    }
    if (ClassUtil.isList(value)) {
      //noinspection unchecked
      return (T[]) ((Collection<?>) value).toArray();
    }
    //noinspection unchecked
    return (T[]) new Object[] {value};
  }
}
