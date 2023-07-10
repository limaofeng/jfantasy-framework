package org.jfantasy.graphql.inputs;

import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;

public class DefaultTypeConverter<T> implements TypeConverter<T> {

  private final Class<T> type;

  public DefaultTypeConverter(Class<T> type) {
    this.type = type;
  }

  @Override
  public T convert(Object value) {
    if (type.isEnum()) {
      //noinspection rawtypes
      return (T) Enum.valueOf((Class<Enum>) type, (String) value);
    }
    return ReflectionUtils.convert(value, type);
  }
}
