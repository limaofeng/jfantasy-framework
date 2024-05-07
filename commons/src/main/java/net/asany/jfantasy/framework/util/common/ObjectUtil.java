package net.asany.jfantasy.framework.util.common;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.toys.CompareResults;
import net.asany.jfantasy.framework.util.error.TransformException;
import net.asany.jfantasy.framework.util.ognl.OgnlUtil;
import net.asany.jfantasy.framework.util.reflect.Property;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class ObjectUtil {

  private static final ConcurrentMap<String, Comparator<?>> COMPARATOR_MAP =
      new ConcurrentHashMap<>();

  private ObjectUtil() {}

  /**
   * COPY 对象
   *
   * @param source 原对象
   * @param target 目标对象
   * @param ignoreProperties 忽略字段
   * @param <T> 类型
   * @return R 目标结果
   */
  public static <T, R> R copy(T source, R target, String... ignoreProperties) {
    BeanUtils.copyProperties(source, target, ignoreProperties);
    return target;
  }

  /**
   * 克隆对象
   *
   * @param object 将要克隆的对象
   * @return 返回的对象
   */
  @SneakyThrows
  @Deprecated
  public static <T> T clone(T object, String... ignoreProperties) {
    if (object == null) {
      return null;
    }
    if (object instanceof String) {
      return object;
    }
    if (object instanceof Number) {
      return object;
    }
    if (object instanceof Map) {
      Map<Object, Object> cloneMap = new HashMap<>();
      //noinspection unchecked
      Map<Object, Object> map = (Map<Object, Object>) object;
      for (Map.Entry<Object, Object> entry : map.entrySet()) {
        cloneMap.put(clone(entry.getKey()), clone(entry.getValue(), ignoreProperties));
      }
      //noinspection unchecked
      return (T) cloneMap;
    }
    if (object instanceof List) {
      List<Object> cloneList = new ArrayList<>();
      List<Object> list = (List<Object>) object;
      for (Object l : list) {
        cloneList.add(clone(l, ignoreProperties));
      }
      return (T) cloneList;
    }
    T target = (T) ClassUtil.newInstance(ClassUtil.getRealClass(object.getClass()));
    assert target != null;
    BeanUtils.copyProperties(object, target, ignoreProperties);
    return target;
  }

  public static <T> T getValue(String key, Object root) {
    //noinspection unchecked
    return (T)
        Arrays.stream((Object[]) key.split("\\."))
            .reduce(
                root,
                (result, next) -> {
                  if (result == null) {
                    return null;
                  }
                  return OgnlUtil.getInstance().getValue((String) next, result);
                });
  }

  public static void setValue(String key, Object root, Object value) {
    OgnlUtil.getInstance().setValue(key, root, value);
  }

  public static <T, C extends Collection<T>> C tree(
      C original, String idKey, String pidKey, String childrenKey) {
    return tree(original, idKey, pidKey, childrenKey, null, null);
  }

  public static <T, C extends Collection<T>> C tree(
      C original, String idKey, String pidKey, String childrenKey, Function<T, T> converter) {
    return tree(original, idKey, pidKey, childrenKey, converter, null);
  }

  public static <T, C extends Collection<T>> C tree(
      C original,
      String idKey,
      String pidKey,
      String childrenKey,
      Comparator<? super T> comparator) {
    return tree(original, idKey, pidKey, childrenKey, null, comparator);
  }

  public static <T, C extends Collection<T>> C tree(
      C original,
      String idKey,
      String pidKey,
      String childrenKey,
      Function<T, T> converter,
      Comparator<? super T> comparator) {
    List<T> items =
        original.stream()
            .map(
                item -> {
                  T target = converter == null ? item : converter.apply(item);
                  setValue(childrenKey, target, new ArrayList<T>());
                  return target;
                })
            .collect(Collectors.toList());
    Stream<T> stream =
        items.stream()
            .filter(
                item -> {
                  T obj = find(items, idKey, getValue(pidKey, item));
                  if (obj == null) {
                    return true;
                  }
                  List<T> children = getValue(childrenKey, obj);
                  children.add(item);
                  if (comparator != null) {
                    children.sort(comparator);
                  }
                  return false;
                });
    return packageResult(stream, original.getClass());
  }

  private static <T, C extends Collection<T>> C packageResult(
      Stream<T> stream, Class<?> resultClass) {
    if (ClassUtil.isList(resultClass)) {
      return (C) stream.collect(Collectors.toList());
    }
    if (ClassUtil.isSet(resultClass)) {
      return (C) stream.collect(Collectors.toSet());
    }
    throw new TransformException("不支持转换到 " + resultClass.getName());
  }

  public static <T, C extends Collection<T>> C flat(C treeData) {
    return flat(treeData, "children");
  }

  public static <T, C extends Collection<T>> C flat(C treeData, String childrenKey) {
    return flat(treeData, childrenKey, null, null);
  }

  public static <T, C extends Collection<T>> C flat(
      C treeData, String childrenKey, String parentName) {
    return flat(treeData, childrenKey, parentName, null);
  }

  public static <T, C extends Collection<T>> C flat(
      C treeData, String childrenKey, String parentName, T parent) {
    List<T> nodes = new ArrayList<>();
    for (T node : treeData) {
      if (parentName != null) {
        setValue(parentName, node, parent);
      }
      nodes.add(node);
      List<T> children = getValue(childrenKey, node);
      if (children == null) {
        continue;
      }
      nodes.addAll(flat(children, childrenKey, parentName, node));
    }
    return packageResult(nodes.stream(), treeData.getClass());
  }

  public static <T, R, C extends Collection<T>, RC extends Collection<R>> RC recursive(
      C treeData, NestedConverter<T, R> converter) {
    NestedContext<R> context = NestedContext.<R>builder().treeData(treeData).build();
    return recursive(treeData, converter, context);
  }

  public static <T, R, C extends Collection<T>, RC extends Collection<R>> RC recursive(
      C treeData, NestedConverter<T, R> converter, String childrenKey) {
    NestedContext<R> context =
        NestedContext.<R>builder().treeData(treeData).childrenKey(childrenKey).build();
    return recursive(treeData, converter, context);
  }

  private static <T, R, C extends Collection<T>, CR extends Collection<R>> CR recursive(
      C treeData, NestedConverter<T, R> converter, NestedContext<R> context) {
    Class<?> listClass = treeData.getClass();
    List<Object> list = packageResult((Stream<Object>) treeData.stream(), List.class);

    int level = context.level;
    R parent = context.parent;

    String childrenKey = context.childrenKey;

    for (int i = 0, len = list.size(); i < len; i++) {
      context.index = i;
      T item = (T) list.get(i);
      R obj = converter.apply(item, context);
      list.set(i, obj);
      if (obj == null) {
        treeData.remove(item);
        continue;
      }
      Collection<T> children =
          getValue(childrenKey, obj.getClass() == item.getClass() ? obj : item);
      if (children == null) {
        continue;
      }
      context.level += 1;
      context.parent = obj;
      setValue(childrenKey, obj, recursive(children, converter, context));
      context.parent = parent;
      context.level = level;
    }

    if (list.size() != treeData.size()) {
      return recursive(treeData, converter, context);
    }

    return packageResult((Stream<R>) list.stream().filter(Objects::nonNull), listClass);
  }

  @Data
  @Builder
  public static class NestedContext<R> {
    @Builder.Default private String childrenKey = "children";
    private Collection<?> treeData;
    private R parent;
    private int index;
    @Builder.Default private int level = 1;
  }

  public interface NestedConverter<T, R> {
    R apply(T t, NestedContext<R> context);
  }

  /**
   * 将集合对象中的 @{fieldName} 对于的值转换为字符串以 @{sign} 连接
   *
   * @param <T> 泛型
   * @param objs 集合
   * @param fieldName 支持ognl表达式
   * @param sign 连接符
   * @return T
   */
  public static <T, C extends Collection<T>> String toString(
      C objs, String fieldName, String sign) {
    AtomicReference<StringBuffer> stringBuffer = new AtomicReference<>(new StringBuffer());
    for (T t : objs) {
      String temp = StringUtil.defaultValue(OgnlUtil.getInstance().getValue(fieldName, t), "");
      if (StringUtil.isBlank(temp)) {
        continue;
      }
      stringBuffer.get().append(sign).append(temp);
    }
    return stringBuffer.get().toString().replaceFirst(sign, "");
  }

  public static <T> String toString(T[] obs, String sign) {
    return toString(obs, null, sign);
  }

  public static <T, C extends Collection<T>> C filter(C list, String fieldName, Object... values) {
    return filter(
        list, item -> ObjectUtil.exists(values, OgnlUtil.getInstance().getValue(fieldName, item)));
  }

  public static <T> T[] filter(T[] objs, String fieldName, Object... values) {
    return filter(
        objs, item -> ObjectUtil.exists(values, OgnlUtil.getInstance().getValue(fieldName, item)));
  }

  public static <T, C extends Collection<T>> T[] toArray(C list, Class<T> type) {
    return list.toArray((T[]) ClassUtil.newInstance(type, list.size()));
  }

  public static <T> String toString(T[] objs, String fieldName, String sign) {
    if (objs.length == 1) {
      if (ClassUtil.isArray(objs[0])) {
        return toString((T[]) objs[0], fieldName, sign);
      } else if (ClassUtil.isList(objs[0])) {
        return toString((List<T>) objs[0], fieldName, sign);
      }
    }
    AtomicReference<StringBuffer> stringBuffer = new AtomicReference<>(new StringBuffer());
    for (T t : objs) {
      String temp =
          StringUtil.isBlank(fieldName)
              ? t.toString()
              : StringUtil.defaultValue(OgnlUtil.getInstance().getValue(fieldName, t), "");
      if (StringUtil.isBlank(temp)) {
        continue;
      }
      stringBuffer.get().append(sign).append(temp);
    }
    return stringBuffer.get().toString().replaceFirst(sign, "");
  }

  public static <T, R, C extends Collection<T>> List<R> toFieldList(
      C list, String fieldName, List<R> returnList) {
    for (Object t : list) {
      returnList.add(OgnlUtil.getInstance().getValue(fieldName, t));
    }
    return returnList;
  }

  public static <T, R> R[] toFieldArray(List<T> objs, String fieldName, Class<R> componentType) {
    R[] returnObjs = (R[]) ClassUtil.newInstance(componentType, objs.size());
    for (int i = objs.size() - 1; i > -1; i--) {
      if (objs.get(i) == null) {
        continue;
      }
      returnObjs[i] = OgnlUtil.getInstance().getValue(fieldName, objs.get(i));
    }
    return returnObjs;
  }

  public static <T, R> R[] toFieldArray(T[] objs, String fieldName, Class<R> componentType) {
    return toFieldArray(objs, fieldName, (R[]) Array.newInstance(componentType, objs.length));
  }

  public static <T, R> R[] toFieldArray(T[] objs, String fieldName, R[] returnObjs) {
    if (returnObjs.length < objs.length) {
      returnObjs =
          (R[]) ClassUtil.newInstance(returnObjs.getClass().getComponentType(), objs.length);
    }
    for (int i = objs.length - 1; i > -1; i--) {
      returnObjs[i] = ClassUtil.getValue(objs[i], fieldName);
    }
    return returnObjs;
  }

  /**
   * 返回 集合中 @{fieldName} 值最大的对象
   *
   * @param <T> 泛型
   * @param c 集合
   * @param fieldName 支持ognl表达式
   * @return T
   */
  public static <T, C extends Collection<T>> T getMaxObject(C c, String fieldName) {
    T maxObject = null;
    for (T element : c) {
      if (maxObject == null) {
        maxObject = element;
      } else {
        if (compareField(maxObject, element, fieldName) == 1) {
          maxObject = element;
        }
      }
    }
    return maxObject;
  }

  /**
   * 返回 集合中 @{fieldName} 值最小的对象
   *
   * @param <T> 泛型
   * @param <C> c 集合
   * @param fieldName 支持ognl表达式
   * @return T
   */
  public static <T, C extends Collection<T>> T getMinObject(C c, String fieldName) {
    T minObject = null;
    for (T element : c) {
      if (minObject == null) {
        minObject = element;
      } else {
        if (compareField(minObject, element, fieldName) == -1) {
          minObject = element;
        }
      }
    }
    return minObject;
  }

  /**
   * 获取集合中 @{field} 的值为 @{value} 的对象 返回索引下标
   *
   * @param <T> 泛型
   * @param objs 原始集合
   * @param field 支持ognl表达式
   * @param value 比较值
   * @return T 如果有多个只返回第一匹配的对象,比较调用对象的 equals 方法
   */
  public static <T, C extends Collection<T>> int indexOf(C objs, String field, Object value) {
    int i = 0;
    for (T obj : objs) {
      Object prop = OgnlUtil.getInstance().getValue(field, obj);
      if (prop == null) {
        continue;
      }
      if (prop.equals(value)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  /**
   * 在集合中查找 @{field} 对于的值为 ${value} 的对象
   *
   * @param <T> 对应的泛型类型
   * @param list 原始集合
   * @param field 支持ognl表达式
   * @param value 比较值
   * @return 返回第一次匹配的对象
   */
  public static <T, C extends Collection<T>> T find(C list, String field, Object value) {
    if (list == null || value == null) {
      return null;
    }
    for (T t : list) {
      Object v = OgnlUtil.getInstance().getValue(field, t);
      if (Objects.equals(value, v)) {
        return t;
      }
    }
    return null;
  }

  public static <T, C extends Collection<T>> T first(C list, String field, Object value) {
    return find(list, field, value);
  }

  public static <T, C extends Collection<T>> T last(C list, String field, Object value) {
    if (list == null) {
      return null;
    }
    List<T> array = packageResult(list.stream(), List.class);
    for (int i = list.size() - 1; i >= 0; i--) {
      T t = array.get(i);
      Object v = OgnlUtil.getInstance().getValue(field, t);
      if (v == value || value.equals(v)) {
        return t;
      }
    }
    return null;
  }

  public static <T> T find(T[] array, Predicate<T> itemSelector) {
    for (T t : array) {
      if (itemSelector.test(t)) {
        return t;
      }
    }
    return null;
  }

  public static <T, C extends Collection<T>> T find(C list, Predicate<T> itemSelector) {
    for (T t : list) {
      if (itemSelector.test(t)) {
        return t;
      }
    }
    return null;
  }

  public static <T> boolean exists(T[] objs, String field, Object value) {
    return find(objs, field, value) != null;
  }

  public static <T, C extends Collection<T>> boolean exists(C list, String field, Object value) {
    return find(list, field, value) != null;
  }

  public static <T> T find(T[] list, String field, Object value) {
    if (list == null) {
      return null;
    }
    for (T t : list) {
      if (t == null) {
        continue;
      }
      Object v = OgnlUtil.getInstance().getValue(field, t);
      if (v == value || value.equals(v)) {
        return t;
      }
    }
    return null;
  }

  public static int indexOf(char[] objs, char c) {
    for (int i = 0; i < objs.length; i++) {
      if (objs[i] == c) {
        return i;
      }
    }
    return -1;
  }

  public static <T> int indexOf(List<T> objs, Predicate<T> selector) {
    for (int i = 0; i < objs.size(); i++) {
      T obj = objs.get(i);
      if (obj == null) {
        continue;
      }
      if (selector.test(obj)) {
        return i;
      }
    }
    return -1;
  }

  public static <T> int indexOf(T[] objs, T o) {
    for (int i = 0; i < objs.length; i++) {
      if (objs[i].equals(o)) {
        return i;
      }
    }
    return -1;
  }

  public static <T, C extends Collection<T>> int indexOf(C objs, T o) {
    return indexOf(objs.toArray(new Object[0]), o);
  }

  public static <T, C extends Collection<T>> int indexOf(C collection, T obj, String property) {
    List<T> objs = packageResult(collection.stream(), List.class);
    for (int i = 0; i < objs.size(); i++) {
      Object value = ClassUtil.getValue(objs.get(i), property);
      if (isNull(value)) {
        continue;
      }
      if (value.equals(ClassUtil.getValue(obj, property))) {
        return i;
      }
    }
    return -1;
  }

  public static <T, C extends Collection<T>> int indexOf(
      C list, String field, String value, boolean ignoreCase) {
    int i = 0;
    for (T obj : list) {
      Object prop = ClassUtil.getValue(obj, field);
      if (prop == null) {
        continue;
      }
      if (ignoreCase ? value.equalsIgnoreCase(StringUtil.nullValue(prop)) : value.equals(prop)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  @SneakyThrows
  public static void setProperties(Object obj, String fieldName, Object value) {
    BeanUtil.setValue(obj, fieldName, value);
  }

  /**
   * 对集合进行排序
   *
   * @param <T> 泛型
   * @param collectoin 要排序的集合
   * @param orderField 排序字段 支持ognl表达式
   * @return T 默认排序方向为 asc
   */
  public static <T, C extends Collection<T>> C sort(C collectoin, String orderField) {
    return sort(collectoin, orderField, "asc");
  }

  /**
   * 对集合进行排序
   *
   * @param <T> 泛型
   * @param collection 要排序的集合
   * @param orderBy 排序字段 支持ognl表达式
   * @param order 排序方向 只能是 asc 与 desc
   * @return T
   */
  public static <T, C extends Collection<T>> C sort(C collection, String orderBy, String order) {
    List<T> list = new ArrayList<>();
    if (collection.isEmpty()) {
      return packageResult(list.stream(), collection.getClass());
    }
    String key = collection.iterator().next().getClass().toString().concat("|").concat(orderBy);
    if (!COMPARATOR_MAP.containsKey(key)) {
      final String orderBys = orderBy;
      COMPARATOR_MAP.put(key, (o1, o2) -> compareField(o1, o2, orderBys));
    }
    list.addAll(collection);
    list.sort((Comparator<T>) COMPARATOR_MAP.get(key));
    if ("desc".equalsIgnoreCase(order)) {
      Collections.reverse(list);
    }
    return packageResult(list.stream(), collection.getClass());
  }

  public static <T, C extends Collection<T>> C sort(C collectoin, Comparator<T> comparator) {
    if (collectoin == null) {
      return null;
    }
    List<T> list;
    if (ClassUtil.isList(collectoin)) {
      list = (List<T>) collectoin;
    } else {
      list = new ArrayList<>(collectoin);
    }
    list.sort(comparator);
    return ClassUtil.isList(collectoin)
        ? (C) list
        : packageResult(list.stream(), collectoin.getClass());
  }

  public static <T> List<T> sort(List<T> collection, String[] customSort, String idFieldName) {
    Class<?> persistentBagClass =
        ClassUtil.forName("org.hibernate.collection.internal.PersistentBag");
    assert persistentBagClass != null;
    boolean isPersistentBag = persistentBagClass.isAssignableFrom(collection.getClass());

    (isPersistentBag ? new ArrayList<>(collection) : collection)
        .sort(new CustomSortOrderComparator(customSort, idFieldName));
    return collection;
  }

  /**
   * 辅助方法 比较两个值的大小
   *
   * @param o1 object1
   * @param o2 object2
   * @param orderField 支持ognl表达式
   * @return int 如果返回1标示 @{o1} 大于 @{o2} <br>
   *     如果返回0标示 @{o1} 等于 @{o2} <br>
   *     如果返回-1标示 @{o1} 小于 @{o2}
   */
  private static int compareField(Object o1, Object o2, String orderField) {
    Object f1 = OgnlUtil.getInstance().getValue(orderField, o1);
    Object f2 = OgnlUtil.getInstance().getValue(orderField, o2);
    if (f1 == f2) {
      return 0;
    }
    if (f1 == null || f2 == null) {
      return -1;
    }
    Object[] ary = {f1, f2};
    if ((f1 instanceof String) && (f2 instanceof String)) {
      Arrays.sort(ary, Collator.getInstance(Locale.CHINA));
    } else {
      Arrays.sort(ary);
    }
    return ary[0].equals(f1) ? -1 : 1;
  }

  public static boolean isNull(Object object) {
    return object == null;
  }

  public static boolean isNotNull(Object object) {
    return !isNull(object);
  }

  public static <T> T defaultValue(T source, T def) {
    return isNull(source) ? def : source;
  }

  public static <T> T defaultValue(T source, Supplier<? extends T> def) {
    return isNull(source) ? def.get() : source;
  }



  public static Map<String, Object> toMap(Object data) {
    Map<Object, Object> alreadyConverted = new HashMap<>();
    return toMap(data, alreadyConverted);
  }

  @SneakyThrows
  private static Map<String, Object> toMap(Object data, Map<Object, Object> alreadyConverted) {
    if (ClassUtil.isMap(data)) {
      //noinspection unchecked
      return (Map<String, Object>) data;
    }
    Map<String, Object> rootMap = new HashMap<>();
    alreadyConverted.put(data, rootMap);

    Property[] properties = ClassUtil.getProperties(ClassUtil.getRealClass(data));
    for (Property property : properties) {
      if (!property.isRead()) {
        continue;
      }
      Object value = property.getValue(data);
      if (value == null || !Hibernate.isInitialized(value)) {
        continue;
      }
      if (alreadyConverted.containsKey(value)) {
        continue;
      }
      if (value instanceof String) {
        rootMap.put(property.getName(), value);
      } else if (ClassUtil.isPrimitive(value)) {
        rootMap.put(property.getName(), value);
      } else if (ClassUtil.isEnum(value)) {
        rootMap.put(property.getName(), value);
      } else if (ClassUtil.isNumber(value)) {
        rootMap.put(property.getName(), value);
      } else if (ClassUtil.isDate(value)) {
        rootMap.put(property.getName(), value);
      } else if (ClassUtil.isArray(value)) {
        rootMap.put(property.getName(), value);
      } else if (ClassUtil.isList(value)) {
        rootMap.put(property.getName(), value);
      } else if (ClassUtil.isMap(value)) {
        rootMap.put(property.getName(), value);
      } else {
        rootMap.put(property.getName(), toMap(value, alreadyConverted));
      }
    }
    return rootMap;
  }

  public static <T> T merge(T dest, T input) {
    return BeanUtil.copyProperties(dest, input, (property, value, target) -> value != null);
  }

  public static <T> T merge(T dest, T input, BeanUtil.PropertyFilter filter) {
    return BeanUtil.copyProperties(dest, input, filter);
  }

  /**
   * 合并数组 并去除重复项
   *
   * @param dest 原数组
   * @param items 要合并的数组
   * @param <T> 泛型
   * @return T[]
   */
  @SafeVarargs
  public static <T> T[] merge(T[] dest, T... items) {
    if (dest == null) {
      return items;
    }
    if (items.length == 0) {
      return dest;
    }
    List<T> ts = new ArrayList<>();
    for (T t : items) {
      if (exists(dest, t) || exists(ts, t)) {
        continue;
      }
      ts.add(t);
    }
    Object array = Array.newInstance(dest.getClass().getComponentType(), dest.length + ts.size());
    for (int i = 0; i < dest.length; i++) {
      Array.set(array, i, dest[i]);
    }
    for (int i = 0; i < ts.size(); i++) {
      Array.set(array, dest.length + i, ts.get(i));
    }
    return (T[]) array;
  }

  @SafeVarargs
  public static <T> T[] join(T[] sources, T... items) {
    if (items.length == 0) {
      return sources;
    }
    List<T> all = new ArrayList<>(Arrays.asList(sources));
    all.addAll(Arrays.asList(items));
    return all.toArray((T[]) Array.newInstance(sources.getClass().getComponentType(), all.size()));
  }

  public static <T, R, C extends Collection<T>, CR extends Collection<R>> CR map(
      C sources, Function<? super T, ? extends R> mapper) {
    return packageResult(sources.stream().map(mapper), sources.getClass());
  }

  public static <R, T> R[] map(
      T[] sources, Function<? super T, ? extends R> mapper, Class<R> returnClass) {
    return Arrays.stream(sources)
        .map(mapper)
        .toArray(length -> (R[]) Array.newInstance(returnClass, length));
  }

  public static <T, C extends Collection<T>> C filter(C sources, Predicate<T> selector) {
    return packageResult(sources.stream().filter(selector), sources.getClass());
  }

  public static <T> T[] filter(T[] sources, Predicate<T> selector) {
    return Arrays.stream(sources)
        .filter(selector)
        .toArray(length -> (T[]) Array.newInstance(sources.getClass().getComponentType(), length));
  }

  public static <T, C extends Collection<T>> void each(C sources, Consumer<T> consumer) {
    sources.forEach(consumer);
  }

  /**
   * 合并集合,去除重复的项
   *
   * @param <T> 泛型
   * @param dest 源集合
   * @param orig 要合并的集合
   */
  public static <T, C extends Collection<T>> void join(C dest, Collection<T> orig) {
    join(dest, orig, "");
  }

  public static <T, C extends Collection<T>> void join(
      C dest, Collection<T> orig, String property) {
    dest.addAll(
        orig.stream()
            .filter(
                item ->
                    StringUtil.isBlank(property)
                        ? !exists(dest, item)
                        : !exists(dest, property, BeanUtil.getValue(item, property)))
            .collect(Collectors.toList()));
  }

  public static <T, C extends Collection<T>> Boolean exists(C list, Predicate<T> selector) {
    for (T t : list) {
      if (selector.test(t)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断对象是否存在于集合中
   *
   * @param <T> 泛型
   * @param list 集合
   * @param object 要判断的对象
   * @return boolean
   */
  public static <T, C extends Collection<T>> Boolean exists(C list, T object) {
    for (Object t : list) {
      if ((t.getClass().isEnum() && t.toString().equals(object)) || t.equals(object)) {
        return true;
      }
    }
    return false;
  }

  public static <T> Boolean exists(T[] array, T object) {
    for (T t : array) {
      if (t.equals(object)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 从集合中删除对象返回被移除的对象,通过属性判断删除
   *
   * @param <T> 泛型
   * @param orig 源集合
   * @param property 判断的字段
   * @param value 字段对应的值
   * @return 被移除的对象
   */
  public static <T, C extends Collection<T>> T remove(C orig, String property, Object value) {
    if (orig == null || value == null) {
      return null;
    }
    List<T> array = packageResult(orig.stream(), List.class);
    int i = indexOf(orig, property, value);
    if (i == -1) {
      return null;
    }
    T t = array.get(i);
    orig.remove(t);
    return t;
  }

  public static <T> T remove(List<T> orig, Predicate<T> selector) {
    if (orig == null) {
      return null;
    }
    int i = indexOf(orig, selector);
    return i == -1 ? null : orig.remove(i);
  }

  /**
   * 从数组中删除对象返回新的数组
   *
   * @param <T> 泛型
   * @param dest 数组
   * @param orig 要删除的对象
   * @return T
   */
  public static <T> T[] remove(T[] dest, T orig) {
    List<T> array = new ArrayList<>(Arrays.asList(dest));
    while (array.contains(orig)) {
      array.remove(orig);
    }
    return array.toArray((T[]) Array.newInstance(dest.getClass().getComponentType(), array.size()));
  }

  /**
   * 获取集合的第一个元素，没有时返回NULL
   *
   * @param <T> 泛型
   * @param list 集合
   * @return T
   */
  public static <T, C extends Collection<T>> T first(C list) {
    List<T> array = packageResult(list.stream(), List.class);
    if (array == null || array.isEmpty()) {
      return null;
    }
    return array.get(0);
  }

  /**
   * 获取数组的第一个元素，没有时返回NULL
   *
   * @param <T> 泛型
   * @param array 数组
   * @return T
   */
  @SafeVarargs
  public static <T> T first(T... array) {
    if (array == null || array.length == 0) {
      return null;
    }
    return array[0];
  }

  /**
   * 获取集合的末尾元素，没有时返回NULL
   *
   * @param <T> 泛型
   * @param collection 集合
   * @return T
   */
  public static <T, C extends Collection<T>> T last(C collection) {
    List<T> list = packageResult(collection.stream(), List.class);
    if (list == null || list.isEmpty()) {
      return null;
    }
    return list.get(list.size() - 1);
  }

  /**
   * 获取数组的末尾元素，没有时返回NULL
   *
   * @param <T> 泛型
   * @param array 数组
   * @return T
   */
  @SafeVarargs
  public static <T> T last(T... array) {
    if (array == null || array.length == 0) {
      return null;
    }
    return array[array.length - 1];
  }

  public static <T> T[] reverse(T[] array) {
    T temp;
    for (int i = 0; i < array.length / 2; i++) {
      temp = array[i];
      array[i] = array[array.length - 1 - i];
      array[array.length - 1 - i] = temp;
    }
    return array;
  }

  public static <T> List<T> reverse(List<T> array) {
    Collections.reverse(array);
    return array;
  }

  private static class CustomSortOrderComparator implements Comparator<Object>, Serializable {

    private final String[] customSort;
    private final String idFieldName;

    private CustomSortOrderComparator(String[] customSort, String idFieldName) {
      this.customSort = Arrays.copyOf(customSort, customSort.length);
      this.idFieldName = idFieldName;
    }

    @Override
    public int compare(Object o1, Object o2) {
      int o1IdKey =
          ObjectUtil.indexOf(
              customSort, OgnlUtil.getInstance().getValue(idFieldName, o1).toString());
      int o2IdKey =
          ObjectUtil.indexOf(
              customSort, OgnlUtil.getInstance().getValue(idFieldName, o2).toString());
      if (o1IdKey == -1 || o2IdKey == -1) {
        return (o1IdKey == o2IdKey) ? 0 : (o2IdKey == -1 ? -1 : 1);
      }
      return (o1IdKey - o2IdKey) > 0 ? 1 : -1;
    }
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

  /**
   * 比较两个集合
   *
   * @param first 第一个集合
   * @param second 第二个集合
   * @param comparator 比较方法
   * @param <T> 类型
   * @return CompareResults
   */
  public static <T> CompareResults<T> compare(
      Collection<T> first, Collection<T> second, Comparator<T> comparator) {
    CompareResults<T> results = new CompareResults<>();
    List<T> olds = new ArrayList<>(first);
    for (T obj : second) {
      if (exists(olds, (Predicate<T>) item -> comparator.compare(item, obj) == 0)) {
        remove(olds, item -> comparator.compare(item, obj) == 0);
        results.getIntersect().add(obj);
      } else {
        results.getExceptB().add(obj);
      }
    }
    results.setExceptA(olds);
    return results;
  }

  public static <T extends SortNode> void resort(
      T node, SortNodeLoader<T> loader, SortNodeUpdater<T> updater) {
    T beforeNode = node.getId() == null ? null : loader.load(node.getId());

    if (beforeNode == null) {
      T parent = loader.load(node.getParentId());
      locomotion(loader, updater, parent, node, node.getId() == null, false);
      if (node.getId() == null) {
        node.setLevel(parent == null ? 1 : parent.getLevel() + 1);
      }
      return;
    }

    if (hasModifyIndex(node, beforeNode)) {
      List<T> nodes = loader.getAll(node.getParentId(), node);
      // 计算 Index
      List<T> siblings = ObjectUtil.sort(nodes, "index", "asc");

      if (node.getIndex() >= siblings.size()) {
        node.setIndex(siblings.size() - 1);
      }

      int oldIndex = beforeNode.getIndex();
      int newIndex = node.getIndex();

      int startIndex = Math.min(oldIndex, newIndex);
      int endIndex = Math.min(Math.max(oldIndex, newIndex), siblings.size() - 1);

      for (T item : siblings.subList(startIndex, endIndex + 1)) {
        if (item.getId().equals(node.getId())) {
          continue;
        }
        item.setIndex(item.getIndex() + (oldIndex > newIndex ? 1 : -1));
        updater.update(item);
      }
    } else if (hasModifyParent(node, beforeNode)) {
      Serializable parentId = node.getParentId();
      Serializable beforeParentId = beforeNode.getParentId();
      // 移入
      locomotion(loader, updater, loader.load(parentId), node, true, true);
      // 移出
      locomotion(loader, updater, loader.load(beforeParentId), beforeNode, false, false);
    }
  }

  public static <T extends SortNode> void updateLevel(
      T node, SortNodeLoader<T> loader, SortNodeUpdater<T> updater) {
    List<T> children = loader.getAll(node.getId(), node);
    for (T item : children) {
      item.setLevel(node.getLevel() + 1);
      updater.update(item);
      updateLevel(item, loader, updater);
    }
  }

  private static <T extends SortNode> void locomotion(
      SortNodeLoader<T> loader,
      SortNodeUpdater<T> updater,
      T parent,
      T node,
      boolean join,
      boolean setAble) {
    List<T> nodes = loader.getAll(parent != null ? parent.getId() : null, node);
    List<T> siblings = ObjectUtil.sort(nodes, "index", "asc");

    for (T item : siblings.subList(Math.min(node.getIndex(), siblings.size()), siblings.size())) {
      if (item.getId().equals(node.getId())) {
        continue;
      }
      item.setIndex(item.getIndex() + (join ? 1 : -1));
      if (join && setAble) {
        item.setLevel(parent == null ? 1 : parent.getLevel() + 1);
        updateLevel(item, loader, updater);
      }
      updater.update(item);
    }
  }

  private static boolean hasModifyIndex(SortNode node, SortNode beforeNode) {
    if (hasModifyParent(node, beforeNode)) {
      return false;
    }

    int oldIndex = beforeNode.getIndex();
    int newIndex = node.getIndex();

    return oldIndex != newIndex;
  }

  private static boolean hasModifyParent(SortNode node, SortNode beforeNode) {

    Serializable oldParentId = beforeNode.getParentId();
    Serializable newParentId = node.getParentId();

    if (newParentId == null && oldParentId != null) {
      return true;
    }
    if (newParentId != null && oldParentId == null) {
      return true;
    }
    return !Objects.equals(newParentId, oldParentId);
  }
}
