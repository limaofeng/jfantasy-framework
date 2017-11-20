package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: 自己封装的 JpaRepository
 * @date 14/11/2017 11:23 AM
 */
public class ComplexJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final EntityManager em;
    private final Class<T> entityClass;
    private final Class<?> idClass;

    public ComplexJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
        this.entityClass = entityInformation.getJavaType();
        this.idClass = entityInformation.getIdType();
    }

    @Override
    public Pager<T> findPager(Pager<T> pager, List<PropertyFilter> filters) {
        PageRequest pageRequest = new PageRequest(pager.getCurrentPage() - 1, pager.getPageSize());
        Page<T> page = this.findAll(new PropertyFilterSpecification(filters), pageRequest);
        pager.reset(Long.valueOf(page.getTotalElements()).intValue(), page.getContent());
        return pager;
    }

    @Override
    public <S extends T> S update(S entity) {
        OgnlUtil ognlUtil = OgnlUtil.getInstance();
        ID id = getIdValue(this.entityClass, entity);
        T oldEntity = this.findOne(id);
        if (entity == oldEntity) {
            return this.em.merge(entity);
        }
        return (S) this.em.merge(merge(entity, oldEntity, this.entityClass, ognlUtil));
    }

    private static <ID> ID getIdValue(Class entityClass, Object entity) {
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

    private Object merge(Object entity, Object oldEntity, Class entityClass, OgnlUtil ognlUtil) {
        // 为普通字段做值转换操作
        this.cleanColumn(entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, Column.class), ognlUtil);
        // 一对一关联关系的表
        this.cleanOneToOne(entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, OneToOne.class), ognlUtil);
        // 多对一关联关系的表
        this.cleanManyToOne(entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, ManyToOne.class), ognlUtil);
        // 多对多关联关系的表
        this.cleanManyToMany(entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, ManyToMany.class), ognlUtil);
        // 一对多关联关系的表
        this.cleanOneToMany(entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, OneToMany.class), ognlUtil);
        return oldEntity;
    }


    private void cleanColumn(Object entity, Object oldEntity, Field[] fields, OgnlUtil ognlUtil) {
        for (Field field : fields) {
            Object value = ognlUtil.getValue(field.getName(), entity);
            if (value != null) {
                ClassUtil.setValue(oldEntity, field.getName(), value);
            }
        }
    }

    private void cleanOneToOne(Object entity, Object oldEntity, Field[] fields, OgnlUtil ognlUtil) {
        for (Field field : fields) {
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            if (!(ObjectUtil.indexOf(oneToOne.cascade(), CascadeType.ALL) > -1 || ObjectUtil.indexOf(oneToOne.cascade(), CascadeType.MERGE) > -1)) {
                continue;
            }
            Object value = ClassUtil.getValue(entity, field.getName());
            if (value == null) {
                continue;
            }
            Object oldValue = ClassUtil.getValue(oldEntity, field.getName());
            if (oldValue == null) {
                ClassUtil.setValue(oldEntity, field.getName(), value);
            } else {
                merge(value, oldValue, ClassUtil.getRealClass(field.getType()), ognlUtil);
            }
        }
    }

    private void cleanManyToOne(Object entity, Object oldEntity, Field[] manyToOneFields, OgnlUtil ognlUtil) {
        for (Field field : manyToOneFields) {
            Object fk = ognlUtil.getValue(field.getName(), entity);
            if (fk == null) {
                ognlUtil.setValue(field.getName(), entity, ognlUtil.getValue(field.getName(), oldEntity));
                continue;
            }
            Serializable fkId = getIdValue(field.getType(), fk);
            Object fkObj = fkId != null ? getSession().get(field.getType(), fkId) : null;
            ognlUtil.setValue(field.getName(), oldEntity == null ? entity : oldEntity, fkObj);
        }
    }

    private void cleanManyToMany(Object entity, Object oldEntity, Field[] manyToManyFields, OgnlUtil ognlUtil) {
        for (Field field : manyToManyFields) {
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            Class targetEntityClass = manyToMany.targetEntity();
            if (void.class == targetEntityClass) {
                targetEntityClass = ClassUtil.getFieldGenericType(field);
            }
            Object fks = ognlUtil.getValue(field.getName(), entity);
            if (!ClassUtil.isList(fks)) {
                continue;
            }
            List<Object> objects = (List<Object>) fks;
            List<Object> addObjects = new ArrayList<>();
            for (Object fk : objects) {
                Serializable fkId = getIdValue(targetEntityClass, fk);
                Object fkObj = fkId != null ? getSession().get(targetEntityClass, fkId) : null;
                if (fkObj != null) {
                    addObjects.add(fkObj);
                }
            }
            ognlUtil.setValue(field.getName(), oldEntity == null ? entity : oldEntity, addObjects);
        }
    }

    private void cleanOneToMany(Object entity, Object oldEntity, Field[] oneToManyFields, OgnlUtil ognlUtil) {
        for (Field field : oneToManyFields) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            Class targetEntityClass = oneToMany.targetEntity();
            if (void.class == targetEntityClass) {
                targetEntityClass = ClassUtil.getFieldGenericType(field);
            }
            if (oneToMany.cascade().length != 0 && !(ObjectUtil.indexOf(oneToMany.cascade(), CascadeType.ALL) > -1 || ObjectUtil.indexOf(oneToMany.cascade(), CascadeType.MERGE) > -1)) {
                continue;
            }
            Object fks = ognlUtil.getValue(field.getName(), entity);
            if (ClassUtil.isList(fks)) {
                List<Object> objects = (List<Object>) fks;
                List<Object> addObjects = new ArrayList<>();
                for (Object fk : objects) {
                    Serializable fkId = getIdValue(targetEntityClass, fk);
                    Object fkObj = fkId != null ? getSession().get(targetEntityClass, fkId) : null;
                    if (fkObj != null) {
                        addObjects.add(BeanUtil.copyProperties(fkObj, fk));
                    } else {
                        addObjects.add(fk);
                    }
                }
                ognlUtil.setValue(field.getName(), oldEntity == null ? entity : oldEntity, addObjects);
                if (oldEntity == null) {
                    continue;
                }
                List<Object> oldFks = ognlUtil.getValue(field.getName(), oldEntity);
                //删除原有数据
                for (Object odl : oldFks) {
                    if (ObjectUtil.find(addObjects, this.getIdName(targetEntityClass), getIdValue(targetEntityClass, odl)) == null) {
                        this.getSession().delete(odl);
                        LOG.debug("删除数据" + getIdValue(targetEntityClass, odl));
                    }
                }
            }
        }
    }

}
