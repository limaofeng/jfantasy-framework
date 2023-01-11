package org.jfantasy.framework.dao.hibernate.util;

import jakarta.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.jfantasy.framework.error.ValidationException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;

/**
 * HibernateUtils 工具类
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019/8/23 10:34 上午
 */
public class HibernateUtils {

  public static IdClass getIdClass(Class entityClass) {
    Class _entityClass = entityClass;
    IdClass idClass;
    do {
      idClass = ClassUtil.getClassGenricType(_entityClass, IdClass.class);
      _entityClass = _entityClass.getSuperclass();
    } while (idClass == null && _entityClass != Object.class);
    return idClass;
  }

  public static <ID> ID getIdValue(Class entityClass, Object entity) {
    OgnlUtil ognlUtil = OgnlUtil.getInstance();
    Field[] idFields = ClassUtil.getDeclaredFields(entityClass, Id.class);
    if (idFields.length == 0) {
      return null;
    }
    if (idFields.length > 1) {
      IdClass idClass = getIdClass(entityClass);
      Serializable id = ClassUtil.newInstance((Class<Serializable>) idClass.value());
      for (Field idField : idFields) {
        ognlUtil.setValue(
            idField.getName(),
            id,
            ognlUtil.getValue(getIdFieldName(idField, idClass.value()), entity));
      }
      return (ID) id;
    } else {
      return ClassUtil.getValue(entity, idFields[0].getName());
    }
  }

  private static String getIdFieldName(Field field, Class idClass) {
    Field fieldByIdClass = ClassUtil.getDeclaredField(idClass, field.getName());
    if (fieldByIdClass.getType() != field.getType()
        && field.getAnnotation(ManyToOne.class) != null) {
      return field.getName() + "." + getIdName(field.getType());
    }
    return field.getName();
  }

  public static <T> String getIdName(Class<T> entityClass) {
    Class clazz = ClassUtil.getRealClass(entityClass);
    Field[] idFields = ClassUtil.getDeclaredFields(clazz, Id.class);
    if (idFields.length == 0) {
      throw new ValidationException("未发现主键配置:" + clazz.getName());
    }
    if (idFields.length > 1) {
      IdClass idClass = getIdClass(entityClass);
      Serializable id = ClassUtil.newInstance((Class<Serializable>) idClass.value());
      StringBuilder idNames = new StringBuilder();
      for (Field idField : idFields) {
        idNames.append(idField.getName());
      }
      return idNames.toString();
    } else {
      return idFields[0].getName();
    }
  }

  public static <T> String getEntityName(Class<T> entityClass) {
    Class clazz = ClassUtil.getRealClass(entityClass);
    Entity entityAnnotation = ClassUtil.getAnnotation(clazz, Entity.class);
    if (entityAnnotation == null) {
      throw new ValidationException("未知实体:" + clazz.getName());
    }
    if (StringUtil.isBlank(entityAnnotation.name())) {
      return clazz.getSimpleName();
    }
    return entityAnnotation.name();
  }

  public static <T> String getTableName(Class<T> entityClass) {
    Class clazz = ClassUtil.getRealClass(entityClass);
    Table entityAnnotation = ClassUtil.getAnnotation(clazz, Table.class);
    if (entityAnnotation == null) {
      throw new ValidationException("未知实体:" + clazz.getName());
    }
    if (StringUtil.isBlank(entityAnnotation.name())) {
      return StringUtil.snakeCase(clazz.getSimpleName()).toUpperCase();
    }
    return entityAnnotation.name();
  }
}
