package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

public class BeanPropertyFilter extends SimpleBeanPropertyFilter {

  @Getter private final Set<Class<?>> types = new HashSet<>();

  private final Map<Class<?>, Set<String>> includeMap = new ConcurrentHashMap<>();

  private final Map<Class<?>, Set<String>> excludeMap = new ConcurrentHashMap<>();

  /**
   * 只返回的字段
   *
   * @param type 类型
   * @param fields 字段
   */
  public BeanPropertyFilter includes(Class<?> type, String... fields) {
    return addToMap(includeMap, type, fields);
  }

  /**
   * 排除字段
   *
   * @param type 类型
   * @param fields 字段
   */
  public BeanPropertyFilter excludes(Class<?> type, String... fields) {
    return addToMap(excludeMap, type, fields);
  }

  private BeanPropertyFilter addToMap(
      Map<Class<?>, Set<String>> map, Class<?> type, String[] fields) {
    if (fields.length == 0) {
      return this;
    }
    types.add(type);
    Set<String> fieldSet = map.getOrDefault(type, new HashSet<>());
    fieldSet.addAll(Arrays.asList(fields));
    map.put(type, fieldSet);
    return this;
  }

  private boolean apply(Class<?> type, String name) {
    Set<String> includeFields = includeMap.get(type);
    Set<String> excludeFields = excludeMap.get(type);
    if (includeFields != null && includeFields.contains(name)) {
      return true;
    } else if (excludeFields != null && !excludeFields.contains(name)) {
      return true;
    } else return includeFields == null && excludeFields == null;
  }

  @Override
  public void serializeAsField(
      Object pojo, JsonGenerator jGen, SerializerProvider prov, PropertyWriter writer)
      throws Exception {
    if (apply(pojo.getClass(), writer.getName())) {
      writer.serializeAsField(pojo, jGen, prov);
    } else if (!jGen.canOmitFields()) {
      writer.serializeAsOmittedField(pojo, jGen, prov);
    }
  }

  public Builder mixin(Class<?> type) {
    return new Builder(this, type);
  }

  public static Builder newBuilder(Class<?> type) {
    return new Builder(type);
  }

  public static class Builder {
    private final BeanPropertyFilter filter;
    private Class<?> current;

    public Builder(BeanPropertyFilter filter, Class<?> type) {
      this.filter = filter;
      this.current = type;
      JSON.mixin(type);
      XML.mixin(type);
    }

    public Builder(Class<?> type) {
      this(new BeanPropertyFilter(), type);
    }

    public BeanPropertyFilter build() {
      return filter;
    }

    public Builder type(Class<?> type) {
      this.current = type;
      JSON.mixin(type);
      return this;
    }

    public Builder excludes(String... names) {
      if (names.length == 0) {
        return this;
      }
      filter.excludes(this.current, names);
      return this;
    }

    public Builder includes(String... names) {
      if (names.length == 0) {
        return this;
      }
      filter.includes(this.current, names);
      return this;
    }
  }
}
