package org.jfantasy.framework.dao.hibernate.util;

import org.jfantasy.framework.error.ValidationException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * HibernateUtils 工具类
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019/8/23 10:34 上午
 */
public class HibernateUtils {

    public static <ID> ID getIdValue(Class entityClass, Object entity) {
        OgnlUtil ognlUtil = OgnlUtil.getInstance();
        Field[] idFields = ClassUtil.getDeclaredFields(entityClass, Id.class);
        if (idFields.length == 0) {
            return null;
        }
        if (idFields.length > 1) {
            IdClass idClass = ClassUtil.getClassGenricType(entityClass, IdClass.class);
            Serializable id = ClassUtil.newInstance((Class<Serializable>) idClass.value());
            for (Field idField : idFields) {
                ognlUtil.setValue(idField.getName(), id, ognlUtil.getValue(idField.getName(), entity));
            }
            return (ID) id;
        } else {
            return (ID) ClassUtil.getValue(entity, idFields[0].getName());
        }
    }

    public static <T> String getIdName(Class<T> entityClass) {
        Class clazz = ClassUtil.getRealClass(entityClass);
        Field[] idFields = ClassUtil.getDeclaredFields(clazz, Id.class);
        if (idFields.length == 0) {
            throw new ValidationException("未发现主键配置:" + clazz.getName());
        }
        if (idFields.length > 1) {
            IdClass idClass = ClassUtil.getClassGenricType(entityClass, IdClass.class);
            Serializable id = ClassUtil.newInstance((Class<Serializable>) idClass.value());
            String idNames = "";
            for (Field idField : idFields) {
                idNames += idField.getName();
            }
            return idNames;
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
