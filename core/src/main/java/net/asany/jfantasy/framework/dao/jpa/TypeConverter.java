package net.asany.jfantasy.framework.dao.jpa;

public interface TypeConverter<T> {
  T convert(Object value);
}
