package org.jfantasy.framework.dao.hibernate.converter;

import java.lang.reflect.Array;
import javax.persistence.AttributeConverter;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 数组转换器
 *
 * @author limaofeng
 */
public class ArrayConverter<T> implements AttributeConverter<T[], String> {

  protected Class<?> entityClass;

  public ArrayConverter() {
    this.entityClass =
        ReflectionUtils.getSuperClassGenricType(ClassUtil.getRealClass(getClass()))
            .getComponentType();
  }

  @Override
  public String convertToDatabaseColumn(T[] attribute) {
    if (attribute == null) {
      return null;
    }
    return JSON.serialize(attribute);
  }

  @Override
  public T[] convertToEntityAttribute(String dbData) {
    if (StringUtil.isBlank(dbData)) {
      return null;
    }
    return (T[]) JSON.deserialize(dbData, Array.newInstance(entityClass, 0).getClass());
  }
}
