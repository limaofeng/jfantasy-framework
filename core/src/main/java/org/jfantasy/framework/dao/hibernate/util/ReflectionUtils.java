package org.jfantasy.framework.dao.hibernate.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfantasy.framework.dao.hibernate.util.converters.DateConverter;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.util.Assert;

/**
 * hibernateDao 用到的一些反射方法
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-12 下午5:03:33
 */
@Slf4j
public final class ReflectionUtils {

  private static final ConvertUtilsBean CONVERT_UTILS =
      BeanUtilsBean.getInstance().getConvertUtils();

  static {
    DateConverter dc = new DateConverter();
    dc.setUseLocaleFormat(true);
    dc.setPatterns(
        new String[] {
          "yyyy-MM",
          "yyyy-MM-dd",
          "yyyy-MM-dd HH:mm:ss",
          "yyyy-MM-dd HH:mm",
          "yyyyMMdd",
          "yyyyMMddHHmmss",
        });
    CONVERT_UTILS.register(dc, Date.class);
  }

  public static Object invokeGetterMethod(Object target, String propertyName) {
    String getterMethodName = "get" + StringUtils.capitalize(propertyName);
    return invokeMethod(target, getterMethodName, new Class[0], new Object[0]);
  }

  public static void invokeSetterMethod(Object target, String propertyName, Object value) {
    invokeSetterMethod(target, propertyName, value, null);
  }

  public static void invokeSetterMethod(
      Object target, String propertyName, Object value, Class<?> propertyType) {
    Class<?> type = propertyType != null ? propertyType : value.getClass();
    String setterMethodName = "set" + StringUtils.capitalize(propertyName);
    invokeMethod(target, setterMethodName, new Class[] {type}, new Object[] {value});
  }

  public static <T> T getFieldValue(Object object, String fieldName) {
    Field field = getDeclaredField(object, fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          "Could not find field [" + fieldName + "] on target [" + object + "]");
    }
    makeAccessible(field);
    Object result = null;
    try {
      result = field.get(object);
    } catch (IllegalAccessException e) {
      log.error("不可能抛出的异常{}", e.getMessage(), e);
    }
    return (T) result;
  }

  public static void setFieldValue(Object object, String fieldName, Object value) {
    Field field = getDeclaredField(object, fieldName);
    if (field == null) {
      throw new IllegalArgumentException(
          "Could not find field [" + fieldName + "] on target [" + object + "]");
    }
    makeAccessible(field);
    try {
      field.set(object, value);
    } catch (IllegalAccessException e) {
      log.error("不可能抛出的异常:{}", e.getMessage(), e);
    }
  }

  public static Object invokeMethod(
      Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
    Method method = getDeclaredMethod(object, methodName, parameterTypes);
    if (method == null) {
      throw new IllegalArgumentException(
          "Could not find method [" + methodName + "] on target [" + object + "]");
    }
    method.setAccessible(true);
    try {
      return method.invoke(object, parameters);
    } catch (Exception e) {
      throw convertReflectionExceptionToUnchecked(e);
    }
  }

  private static Field getDeclaredField(Object object, String fieldName) {
    Assert.notNull(object, "object不能为空");
    Assert.hasText(fieldName, "fieldName");
    for (Class<?> superClass = object.getClass(); superClass != Object.class; ) {
      try {
        return superClass.getDeclaredField(fieldName);
      } catch (NoSuchFieldException localNoSuchFieldException) {
        log.debug(localNoSuchFieldException.getMessage());
        superClass = superClass.getSuperclass();
      }
    }

    return null;
  }

  private static void makeAccessible(Field field) {
    if ((!Modifier.isPublic(field.getModifiers()))
        || (!Modifier.isPublic(field.getDeclaringClass().getModifiers()))) {
      field.setAccessible(true);
    }
  }

  private static Method getDeclaredMethod(
      Object object, String methodName, Class<?>[] parameterTypes) {
    Assert.notNull(object, "object不能为空");
    for (Class<?> superClass = object.getClass(); superClass != Object.class; ) {
      try {
        return superClass.getDeclaredMethod(methodName, parameterTypes);
      } catch (NoSuchMethodException localNoSuchMethodException) {
        log.error(localNoSuchMethodException.getMessage(), localNoSuchMethodException);
        superClass = superClass.getSuperclass();
      }
    }
    return null;
  }

  public static <T> Class<T> getSuperClassGenricType(Class clazz) {
    return getSuperClassGenricType(clazz, 0);
  }

  public static Class getInterfaceGenricType(Class clazz, Class interfaceClazz) {
    return getInterfaceGenricType(clazz, interfaceClazz, 0);
  }

  public static <T> Class<T> getInterfaceGenricType(Class clazz, Class interfaceClazz, int index) {
    return ClassUtil.getInterfaceGenricType(clazz, interfaceClazz, index);
  }

  public static <T> Class<T> getSuperClassGenricType(Class clazz, int index) {
    return ClassUtil.getSuperClassGenricType(clazz, index);
  }

  public static List<Object> convertElementPropertyToList(
      Collection<Object> collection, String propertyName) {
    List<Object> list = new ArrayList<>();
    try {
      for (Object obj : collection) {
        list.add(PropertyUtils.getProperty(obj, propertyName));
      }
    } catch (Exception e) {
      throw convertReflectionExceptionToUnchecked(e);
    }
    return list;
  }

  public static String convertElementPropertyToString(
      Collection<Object> collection, String propertyName, String separator) {
    List<Object> list = convertElementPropertyToList(collection, propertyName);
    return StringUtils.join(list, separator);
  }

  public static <T> T convertStringToObject(String value, Class<T> toType) {
    try {
      return toType.cast(CONVERT_UTILS.convert(value, toType));
    } catch (Exception e) {
      throw convertReflectionExceptionToUnchecked(e);
    }
  }

  public static <T> T convert(Object value, Class<T> toType) {
    return toType.cast(CONVERT_UTILS.convert(value, toType));
  }

  public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
    if ((e instanceof IllegalAccessException)
        || (e instanceof IllegalArgumentException)
        || (e instanceof NoSuchMethodException)) {
      return new IllegalArgumentException("Reflection Exception.", e);
    }
    if (e instanceof InvocationTargetException) {
      return new RuntimeException(
          "Reflection Exception.", ((InvocationTargetException) e).getTargetException());
    }
    if (e instanceof RuntimeException) {
      return (RuntimeException) e;
    }
    return new RuntimeException("Unexpected Checked Exception.", e);
  }
}
