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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.util.common.ClassUtil;

public class Property {
  private final PropertyDescriptor descriptor;
  private final String name;
  private final MethodProxy readMethodProxy;
  private final MethodProxy writeMethodProxy;
  private final Class<?> propertyType;
  private final boolean write;
  private final boolean read;
  private final Map<Class<?>, Annotation> annotationCache = new HashMap<>();

  public Property(PropertyDescriptor descriptor) {
    this.name = descriptor.getName();
    this.readMethodProxy =
        descriptor.getReadMethod() == null ? null : new MethodProxy(descriptor.getReadMethod());
    this.writeMethodProxy =
        descriptor.getWriteMethod() == null
            ? null
            : new MethodProxy(descriptor.getWriteMethod(), descriptor.getPropertyType());
    this.read = this.readMethodProxy != null;
    this.write = this.writeMethodProxy != null;
    this.propertyType = descriptor.getPropertyType();
    this.descriptor = descriptor;
  }

  public boolean isTransient() {
    Object value = this.descriptor.getValue("transient");
    return (value instanceof Boolean) ? (Boolean) value : false;
  }

  public boolean isWrite() {
    return this.write;
  }

  public boolean isRead() {
    return this.read;
  }

  public <T> T getValue(Object target) {
    if (!this.read) {
      return null;
    }
    return (T) this.readMethodProxy.invoke(target);
  }

  public void setValue(Object target, Object value) {
    if (!this.write) {
      return;
    }
    this.writeMethodProxy.invoke(target, value);
  }

  public <T> Class<T> getPropertyType() {
    return (Class<T>) this.propertyType;
  }

  public String getName() {
    return this.name;
  }

  public <T extends Annotation> T getAnnotation(Class<T> tClass) {
    if (annotationCache.containsKey(tClass)) {
      return (T) annotationCache.get(tClass);
    }
    T annotation = null;
    Class<?> declaringClass = null;
    if (this.isRead()) {
      annotation = this.getReadMethod().getAnnotation(tClass);
      if (annotation == null) {
        declaringClass = this.getReadMethod().getDeclaringClass();
      }
    }
    if (annotation == null && this.isWrite()) {
      annotation = this.getWriteMethod().getAnnotation(tClass);
      if (annotation == null) {
        declaringClass = this.getWriteMethod().getDeclaringClass();
      }
    }
    if (annotation == null) {
      Field field = ClassUtil.getDeclaredField(declaringClass, this.name);
      if (field != null) {
        annotation = field.getAnnotation(tClass);
      }
    }
    annotationCache.put(tClass, annotation);
    return annotation;
  }

  public MethodProxy getReadMethod() {
    return this.readMethodProxy;
  }

  public MethodProxy getWriteMethod() {
    return this.writeMethodProxy;
  }

  public ParameterizedType getGenericType() {
    return (ParameterizedType) this.getReadMethod().getMethod().getGenericReturnType();
  }
}
