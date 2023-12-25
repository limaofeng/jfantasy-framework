package net.asany.jfantasy.framework.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public interface IClass<T> {
  Property getProperty(String paramString);

  Property[] getProperties();

  T newInstance();

  T newInstance(Object paramObject);

  T newInstance(Class<?> paramClass, Object paramObject) throws InvocationTargetException;

  MethodProxy getMethod(String paramString);

  MethodProxy getMethod(String paramString, Class<?>[] paramArrayOfClass);

  void setValue(Object paramObject1, String paramString, Object paramObject2);

  <V> V getValue(Object paramObject, String paramString);

  T newInstance(Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject);

  Field[] getDeclaredFields();

  Field getDeclaredField(String fieldName);

  Field[] getDeclaredFields(Class<? extends Annotation> annotClass);

  <V> V getValue(String name);
}
