package net.asany.jfantasy.framework.dao.hibernate.converter;

import jakarta.persistence.AttributeConverter;
import java.util.List;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * List转换器
 *
 * @author limaofeng
 */
public class ListConverter<T> implements AttributeConverter<List<T>, String> {

  protected Class<T> entityClass;

  public ListConverter() {
    this.entityClass = ReflectionUtils.getSuperClassGenricType(ClassUtil.getRealClass(getClass()));
  }

  @Override
  public String convertToDatabaseColumn(List<T> attribute) {
    if (attribute == null) {
      return null;
    }
    return JSON.serialize(attribute);
  }

  @Override
  public List<T> convertToEntityAttribute(String dbData) {
    if (StringUtil.isBlank(dbData)) {
      return null;
    }
    //noinspection unchecked
    return JSON.deserialize(dbData, List.class, entityClass);
  }
}
