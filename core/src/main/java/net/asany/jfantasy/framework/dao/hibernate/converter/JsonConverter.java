package net.asany.jfantasy.framework.dao.hibernate.converter;

import jakarta.persistence.AttributeConverter;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * Json转换器
 *
 * @author limaofeng
 */
public class JsonConverter<T> implements AttributeConverter<T, String> {

  protected Class<T> entityClass;

  public JsonConverter() {
    this.entityClass = ReflectionUtils.getSuperClassGenricType(ClassUtil.getRealClass(getClass()));
  }

  @Override
  public String convertToDatabaseColumn(T attribute) {
    if (attribute == null) {
      return null;
    }
    return JSON.serialize(attribute);
  }

  @Override
  public T convertToEntityAttribute(String dbData) {
    if (StringUtil.isBlank(dbData)) {
      return null;
    }
    return JSON.deserialize(dbData, entityClass);
  }
}
