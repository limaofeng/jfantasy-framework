package net.asany.jfantasy.framework.dao.hibernate.converter;

import jakarta.persistence.AttributeConverter;
import java.lang.reflect.Array;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

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
