package org.jfantasy.graphql.inputs;

/**
 * 无类型转换
 *
 * @param <T>
 * @author limaofeng
 */
public class NothingTypeConverter<T> implements TypeConverter<T> {

  @Override
  public T convert(Object value) {
    //noinspection unchecked
    return (T) value;
  }
}
