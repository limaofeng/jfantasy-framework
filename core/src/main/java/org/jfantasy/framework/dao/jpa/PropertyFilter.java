package org.jfantasy.framework.dao.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.util.common.ClassUtil;

/**
 * 属性过滤器
 *
 * @author limaofeng
 */
public interface PropertyFilter {

  /**
   * 通过 entityClass 创建 PropertyFilter
   *
   * @param entityClass 实体类型
   * @return PropertyFilter
   */
  static PropertyFilter newFilter(Class<?> entityClass) {
    Class<? extends PropertyFilter> filterClass = PropertyFilterBuilder.FILTERS.get(entityClass);
    if (filterClass == null) {
      return new JpaDefaultPropertyFilter(entityClass, new ArrayList<>());
    }
    return ClassUtil.newInstance(filterClass);
  }

  /**
   * 构造器
   *
   * @return PropertyFilter
   */
  static JpaDefaultPropertyFilter newFilter() {
    return newFilter(new ArrayList<>());
  }

  /**
   * 构造器
   *
   * <p>使用的是 JpaDefaultPropertyFilterBuilder
   *
   * @param predicates 预设筛选条件
   * @return PropertyFilter
   */
  static JpaDefaultPropertyFilter newFilter(List<PropertyPredicate> predicates) {
    return new JpaDefaultPropertyFilter(predicates);
  }

  /**
   * 等于
   *
   * @param name 字段名
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  <T> PropertyFilter equal(String name, T value);

  /**
   * 模糊查询
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter contains(String name, String value);

  /**
   * 模糊查询 (不包含)
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter notContains(String name, String value);

  /**
   * 模糊查询 (匹配开始)
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter startsWith(String name, String value);

  /**
   * 模糊查询 (不匹配开始)
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter notStartsWith(String name, String value);

  /**
   * 模糊查询 (匹配结束)
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter endsWith(String name, String value);

  /**
   * 模糊查询 (不匹配结束)
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter notEndsWith(String name, String value);

  /**
   * 小于
   *
   * @param name 字段名
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  <T> PropertyFilter lessThan(String name, T value);

  /**
   * 大于
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter greaterThan(String name, Object value);

  /**
   * 小于等于
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter lessThanOrEqual(String name, Object value);

  /**
   * 大于等于
   *
   * @param name 字段名
   * @param value 值
   * @return PropertyFilter
   */
  PropertyFilter greaterThanOrEqual(String name, Object value);

  /**
   * in
   *
   * @param name 名称
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  @SuppressWarnings("unchecked")
  <T> PropertyFilter in(String name, T... value);

  /**
   * in
   *
   * @param name 名称
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  <T> PropertyFilter in(String name, List<T> value);

  /**
   * not in
   *
   * @param name 名称
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  @SuppressWarnings("unchecked")
  <T> PropertyFilter notIn(String name, T... value);

  /**
   * 不等于
   *
   * @param name 名称
   * @param value 值
   * @param <T> 泛型
   * @return PropertyFilter
   */
  <T> PropertyFilter notEqual(String name, T value);

  /**
   * is null
   *
   * @param name 字段名
   * @return PropertyFilter
   */
  PropertyFilter isNull(String name);

  /**
   * not null
   *
   * @param name 字段名
   * @return PropertyFilter
   */
  PropertyFilter isNotNull(String name);

  /**
   * is empty
   *
   * @param name 字段名
   * @return PropertyFilter
   */
  PropertyFilter isEmpty(String name);

  /**
   * is not empty
   *
   * @param name 字段名
   * @return PropertyFilter
   */
  PropertyFilter isNotEmpty(String name);

  /**
   * between
   *
   * @param name 字段名
   * @param x 开始值
   * @param y 结束值
   * @param <Y> 泛型
   * @return PropertyFilter
   */
  <Y extends Comparable<? super Y>> PropertyFilter between(String name, Y x, Y y);

  /**
   * and 连接
   *
   * @param builders PropertyFilter
   * @return PropertyFilter
   */
  PropertyFilter and(PropertyFilter... builders);

  /**
   * or 连接
   *
   * @param filters PropertyFilter
   * @return PropertyFilter
   */
  PropertyFilter or(PropertyFilter... filters);

  /**
   * not 连接
   *
   * @param filters PropertyFilter
   * @return PropertyFilter
   */
  PropertyFilter not(PropertyFilter... filters);

  /**
   * 构建
   *
   * @param <T> 泛型
   * @return T
   */
  <T> T build();

  /**
   * 注册一个自定义的 PropertyFilter 并与 entityClass 绑定
   *
   * @param entityClass 实体类型
   * @param filterClass PropertyFilter 类型
   */
  static void register(Class<?> entityClass, Class<? extends PropertyFilter> filterClass) {
    PropertyFilterBuilder.FILTERS.put(entityClass, filterClass);
  }

  /**
   * 自定义 Entity 对应的 属性及 value 转换器
   *
   * @param customizer 定制器
   */
  static void custom(PropertyFilterCustomizer<Class<?>> customizer) {
    Class<?> entityClass = ReflectionUtils.getSuperClassGenricType(customizer.getClass(), 0);
    Map<String, TypeConverter<?>> typeConverterMap =
        PropertyFilterBuilder.initDefaultConverters(entityClass);
    Map<String, PropertyDefinition<?>> propertyDefinitionMap =
        PropertyFilterBuilder.CUSTOM_PROPERTIES.computeIfAbsent(
            entityClass, (clazz) -> new HashMap<>());
    customizer.customize(typeConverterMap, propertyDefinitionMap);
  }
}
