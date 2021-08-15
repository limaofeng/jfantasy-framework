package org.jfantasy.graphql.inputs;

public interface TypeConverter<T> {
  T convert(Object value);
}
