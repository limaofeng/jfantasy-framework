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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.collection.spi.PersistentMap;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.engine.internal.MutableEntityEntry;

public class HibernateCloningHelper {
  public static <T> T cloneEntity(T entity) {
    if (entity == null) {
      return null;
    }

    Map<Object, Object> alreadyCloned = new HashMap<>();
    return cloneEntityInternal(entity, alreadyCloned);
  }

  private static <T> T cloneEntityInternal(T entity, Map<Object, Object> alreadyCloned) {
    if (entity == null) {
      return null;
    }

    // Return the entity directly if it is a cloneable simple type or has been cloned already
    if (isSimpleType(entity.getClass()) || alreadyCloned.containsKey(entity)) {
      return entity;
    }

    try {
      boolean isMutableEntity = entity instanceof MutableEntityEntry;
      //noinspection unchecked
      Class<T> entityClass =
          isMutableEntity
              ? (Class<T>) Class.forName(((MutableEntityEntry) entity).getEntityName())
              : (Class<T>) entity.getClass();

      T cloned = entityClass.getDeclaredConstructor().newInstance();
      alreadyCloned.put(entity, cloned);

      for (Field field : entityClass.getDeclaredFields()) {
        if (field.getName().startsWith("$$_hibernate_")) {
          continue;
        }
        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }
        field.setAccessible(true);
        Object fieldValue =
            isMutableEntity ? ObjectUtil.getValue(field.getName(), entity) : field.get(entity);

        if (fieldValue == null || !Hibernate.isInitialized(fieldValue)) {
          continue;
        }

        Object clonedValue;
        if (field.getType().isArray()) {
          clonedValue = cloneArray(fieldValue, alreadyCloned);
        } else if (fieldValue instanceof Collection) {
          clonedValue = cloneCollection((Collection<?>) fieldValue, alreadyCloned);
        } else if (fieldValue instanceof Map) {
          clonedValue = cloneMap((Map<?, ?>) fieldValue, alreadyCloned);
        } else if (!isSimpleType(field.getType())) {
          clonedValue = cloneEntityInternal(fieldValue, alreadyCloned);
        } else {
          clonedValue = fieldValue;
        }
        field.set(cloned, clonedValue);
      }
      return cloned;
    } catch (Exception e) {
      throw new RuntimeException("Cloning failed", e);
    }
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
        || clazz == Byte.class;
  }

  private static Object cloneArray(Object array, Map<Object, Object> alreadyCloned) {
    int length = Array.getLength(array);
    Object clonedArray = Array.newInstance(array.getClass().getComponentType(), length);
    for (int i = 0; i < length; i++) {
      Object arrayItem = Array.get(array, i);
      Object clonedItem =
          isSimpleType(arrayItem.getClass())
              ? arrayItem
              : cloneEntityInternal(arrayItem, alreadyCloned);
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
    //noinspection unchecked
    Collection<Object> clonedCollection =
        (Collection<Object>) collectionClazz.getConstructor().newInstance();
    for (Object item : collection) {
      clonedCollection.add(cloneEntityInternal(item, alreadyCloned));
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
    //noinspection unchecked
    Map<Object, Object> clonedMap = (Map<Object, Object>) mapClazz.getConstructor().newInstance();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      clonedMap.put(
          cloneEntityInternal(entry.getKey(), alreadyCloned),
          cloneEntityInternal(entry.getValue(), alreadyCloned));
    }
    return clonedMap;
  }
}
