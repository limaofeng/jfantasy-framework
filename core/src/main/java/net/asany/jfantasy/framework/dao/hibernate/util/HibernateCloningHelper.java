/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.dao.hibernate.util;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.*;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.reflect.Property;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentMap;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.engine.internal.MutableEntityEntry;

public class HibernateCloningHelper {

  public static <T> T cloneEntity(T entity) {
    Map<Object, Object> alreadyCloned = new HashMap<>();
    return cloneValueInternal(entity, alreadyCloned);
  }

  private static <T> T cloneValueInternal(T value, Map<Object, Object> alreadyCloned) {
    if (value == null) {
      return null;
    }
    Class<T> valueClass = ClassUtil.getRealClass(value);
    // Return the entity directly if it is a cloneable simple type or has been cloned already
    if (isSimpleType(valueClass)) {
      return value;
    }
    if (alreadyCloned.containsKey(value)) {
      return (T) alreadyCloned.get(value);
    }
    try {
      Object clonedValue;
      if (valueClass.isArray()) {
        clonedValue = cloneArray(value, alreadyCloned);
      } else if (value instanceof Collection<?> collection) {
        clonedValue = cloneCollection(collection, alreadyCloned);
      } else if (value instanceof Map) {
        clonedValue = cloneMap((Map<?, ?>) value, alreadyCloned);
      } else if (!isSimpleType(valueClass)) {
        clonedValue = cloneEntityInternal(value, alreadyCloned);
      } else {
        clonedValue = value;
      }
      return (T) clonedValue;
    } catch (Exception e) {
      throw new RuntimeException("Cloning failed", e);
    }
  }

  private static <T> Class<T> getRealClass(T entity) throws ClassNotFoundException {
    if (entity instanceof MutableEntityEntry) {
      return (Class<T>) Class.forName(((MutableEntityEntry) entity).getEntityName());
    }
    return (Class<T>) Hibernate.getClass(entity);
  }

  private static <T> T cloneEntityInternal(T entity, Map<Object, Object> alreadyCloned)
      throws ClassNotFoundException,
          NoSuchMethodException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    Class<T> entityClass = getRealClass(entity);

    T cloned = entityClass.getDeclaredConstructor().newInstance();
    alreadyCloned.put(entity, cloned);

    for (Field field : ClassUtil.getDeclaredFields(entityClass)) {
      if (field.getName().startsWith("$$_hibernate_")) {
        continue;
      }
      if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
        continue;
      }

      Property property = ClassUtil.getProperty(entityClass, field.getName());

      if (property == null) {
        continue;
      }
      if (property.isTransient() || !property.isRead() || !property.isWrite()) {
        continue;
      }

      Object fieldValue = ObjectUtil.getValue(field.getName(), entity);
      if (fieldValue == null) {
        continue;
      }

      Object copyFieldValue;

      if (!Hibernate.isInitialized(fieldValue)) {
        copyFieldValue = cloneLazyEntityInternal(fieldValue, alreadyCloned);
      } else {
        copyFieldValue = cloneValueInternal(fieldValue, alreadyCloned);
      }

      if (copyFieldValue == null) {
        continue;
      }

      ObjectUtil.setValue(field.getName(), cloned, copyFieldValue);
    }
    return cloned;
  }

  public static <T> T cloneLazyEntityInternal(T entity, Map<Object, Object> alreadyCloned) {
    Class<T> entityClass = ClassUtil.getRealClass(entity);
    if (!isEntityClass(entityClass)) {
      return null;
    }
    if (alreadyCloned.containsKey(entity)) {
      return (T) alreadyCloned.get(entity);
    }
    EntityManager em = SpringBeanUtils.getBeanByType(EntityManager.class);
    Session session = em.unwrap(Session.class);
    Object primaryKey = session.getIdentifier(entity);
    T copyFieldValue = createEntityWithId(entityClass, primaryKey);
    alreadyCloned.put(entity, copyFieldValue);
    return copyFieldValue;
  }

  public static boolean isEntityClass(Class<?> clazz) {
    EntityManager em = SpringBeanUtils.getBeanByType(EntityManager.class);
    Metamodel metamodel = em.getMetamodel();

    for (EntityType<?> entityType : metamodel.getEntities()) {
      if (entityType.getJavaType().equals(clazz)) {
        return true;
      }
    }

    return false;
  }

  private static boolean isSimpleType(Class<?> clazz) {
    return clazz.isEnum()
        || clazz.isPrimitive()
        || clazz == String.class
        || clazz == Integer.class
        || clazz == Long.class
        || clazz == Boolean.class
        || clazz == Double.class
        || clazz == Float.class
        || clazz == Short.class
        || clazz == Character.class
        || clazz == Byte.class
        || Date.class.isAssignableFrom(clazz)
        || LocalDateTime.class.isAssignableFrom(clazz)
        || Number.class.isAssignableFrom(clazz);
  }

  private static Object cloneArray(Object array, Map<Object, Object> alreadyCloned) {
    int length = Array.getLength(array);
    Object clonedArray = Array.newInstance(array.getClass().getComponentType(), length);
    for (int i = 0; i < length; i++) {
      Object arrayItem = Array.get(array, i);
      Object clonedItem =
          isSimpleType(arrayItem.getClass())
              ? arrayItem
              : cloneValueInternal(arrayItem, alreadyCloned);
      Array.set(clonedArray, i, clonedItem);
    }
    return clonedArray;
  }

  private static Collection<?> cloneCollection(
      Collection<?> collection, Map<Object, Object> alreadyCloned)
      throws InstantiationException,
          IllegalAccessException,
          NoSuchMethodException,
          InvocationTargetException {
    Class<?> collectionClazz = collection.getClass();
    if (collection instanceof PersistentSet) {
      collectionClazz = LinkedHashSet.class;
    } else if (collection instanceof PersistentList) {
      collectionClazz = ArrayList.class;
    }
    Collection<Object> clonedCollection =
        (Collection<Object>) collectionClazz.getConstructor().newInstance();
    for (Object item : collection) {
      clonedCollection.add(cloneValueInternal(item, alreadyCloned));
    }
    return clonedCollection;
  }

  private static Map<?, ?> cloneMap(Map<?, ?> map, Map<Object, Object> alreadyCloned)
      throws InstantiationException,
          IllegalAccessException,
          NoSuchMethodException,
          InvocationTargetException {
    Class<?> mapClazz = map.getClass();
    if (map instanceof PersistentMap) {
      mapClazz = LinkedHashMap.class;
    }
    Map<Object, Object> clonedMap = (Map<Object, Object>) mapClazz.getConstructor().newInstance();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      clonedMap.put(
          cloneValueInternal(entry.getKey(), alreadyCloned),
          cloneValueInternal(entry.getValue(), alreadyCloned));
    }
    return clonedMap;
  }

  public static <T> T createEntityWithId(Class<T> entityClass, Object idOrCompositeId) {
    try {
      // 创建新的实体实例
      T entity = entityClass.getDeclaredConstructor().newInstance();

      // 查找主键字段或嵌入式主键字段
      Field idField = null;
      boolean isEmbeddedId = false;

      for (Field field : ClassUtil.getDeclaredFields(entityClass)) {
        if (field.isAnnotationPresent(Id.class)) {
          idField = field;
          break;
        } else if (field.isAnnotationPresent(EmbeddedId.class)) {
          idField = field;
          isEmbeddedId = true;
          break;
        }
      }
      if (idField != null) {
        if (isEmbeddedId) {
          // 处理 @EmbeddedId
          idField.set(entity, idOrCompositeId);
        } else {
          if (entityClass.isAnnotationPresent(IdClass.class)) {
            // 处理 @IdClass
            for (Field pkField : ClassUtil.getDeclaredFields(idOrCompositeId.getClass())) {
              Object pkValue = ClassUtil.getFieldValue(idOrCompositeId, pkField.getName());
              ClassUtil.setFieldValue(entity, pkField.getName(), pkValue);
            }
          } else {
            // 处理单一 @Id
            ClassUtil.setFieldValue(entity, idField.getName(), idOrCompositeId);
          }
        }
      } else {
        throw new RuntimeException(
            "No @Id or @EmbeddedId field found in entity class: " + entityClass.getName());
      }

      return entity;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create entity and set ID", e);
    }
  }
}
