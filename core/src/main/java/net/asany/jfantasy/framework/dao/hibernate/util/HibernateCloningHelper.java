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
import java.time.LocalDateTime;
import java.util.*;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.hibernate.Hibernate;
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
    // Return the entity directly if it is a cloneable simple type or has been cloned already
    if (isSimpleType(value.getClass())) {
      return value;
    }
    if (alreadyCloned.containsKey(value)) {
      //noinspection unchecked
      return (T) alreadyCloned.get(value);
    }
    try {
      Object clonedValue;
      if (value.getClass().isArray()) {
        clonedValue = cloneArray(value, alreadyCloned);
      } else if (value instanceof Collection<?> collection) {
        clonedValue = cloneCollection(collection, alreadyCloned);
      } else if (value instanceof Map) {
        clonedValue = cloneMap((Map<?, ?>) value, alreadyCloned);
      } else if (!isSimpleType(value.getClass())) {
        clonedValue = cloneEntityInternal(value, alreadyCloned);
      } else {
        clonedValue = value;
      }
      //noinspection unchecked
      return (T) clonedValue;
    } catch (Exception e) {
      throw new RuntimeException("Cloning failed", e);
    }
  }

  private static <T> Class<T> getRealClass(T entity) throws ClassNotFoundException {
    if (entity instanceof MutableEntityEntry) {
      //noinspection unchecked
      return (Class<T>) Class.forName(((MutableEntityEntry) entity).getEntityName());
    }
    //noinspection unchecked
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

    for (Field field : entityClass.getDeclaredFields()) {
      if (field.getName().startsWith("$$_hibernate_")) {
        continue;
      }
      if (Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      Object fieldValue = ObjectUtil.getValue(field.getName(), entity);

      if (fieldValue == null || !Hibernate.isInitialized(fieldValue)) {
        continue;
      }

      ObjectUtil.setValue(field.getName(), cloned, cloneValueInternal(fieldValue, alreadyCloned));
    }
    return cloned;
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
    //noinspection unchecked
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
    //noinspection unchecked
    Map<Object, Object> clonedMap = (Map<Object, Object>) mapClazz.getConstructor().newInstance();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      clonedMap.put(
          cloneValueInternal(entry.getKey(), alreadyCloned),
          cloneValueInternal(entry.getValue(), alreadyCloned));
    }
    return clonedMap;
  }
}
