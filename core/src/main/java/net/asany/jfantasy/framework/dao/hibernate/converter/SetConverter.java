package net.asany.jfantasy.framework.dao.hibernate.converter;

import jakarta.persistence.AttributeConverter;
import java.util.Set;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * 集合
 *
 * @author limaofeng
 */
public class SetConverter<T> implements AttributeConverter<Set<T>, String> {

  protected Class<T> entityClass;

  public SetConverter() {
    this.entityClass = ReflectionUtils.getSuperClassGenricType(ClassUtil.getRealClass(getClass()));
  }

  @Override
  public String convertToDatabaseColumn(Set<T> attribute) {
    if (attribute == null) {
      return null;
    }
    return JSON.serialize(attribute);
  }

  @Override
  public Set<T> convertToEntityAttribute(String dbData) {
    if (StringUtil.isBlank(dbData)) {
      return null;
    }
    //noinspection unchecked
    return JSON.deserialize(dbData, Set.class, entityClass);
  }
}
