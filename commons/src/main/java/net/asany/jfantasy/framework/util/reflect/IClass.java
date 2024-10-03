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
