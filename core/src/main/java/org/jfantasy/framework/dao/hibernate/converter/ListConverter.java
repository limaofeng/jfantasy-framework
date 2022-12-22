package org.jfantasy.framework.dao.hibernate.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import javax.persistence.AttributeConverter;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;

public class ListConverter<T> implements AttributeConverter<List<T>, String> {

  protected Class entityClass;

  public ListConverter() {
    this.entityClass =
        ReflectionUtils.getSuperClassGenricType(ClassUtil.getRealClass(getClass()))
            .getComponentType();
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
    return JSON.deserialize(dbData, new TypeReference<List<T>>() {});
  }
}
