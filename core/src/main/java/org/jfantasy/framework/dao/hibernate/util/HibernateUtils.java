package org.jfantasy.framework.dao.hibernate.util;

import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;

import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
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
            return (ID)id;
        } else {
            return (ID) ClassUtil.getValue(entity, idFields[0].getName());
        }
    }

}
