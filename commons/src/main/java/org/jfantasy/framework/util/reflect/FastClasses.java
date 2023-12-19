package org.jfantasy.framework.util.reflect;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * FastClass 实现
 *
 * @author limaofeng
 */
@Slf4j
public class FastClasses<T> implements IClass<T> {
  private final Class<T> clazz;
  private final Map<String, Property> properties = new HashMap<>();
  private final Map<String, MethodProxy> methodProxies = new HashMap<>();
  private final Map<Class<?>, Constructor<?>> constructors = new HashMap<>();
  private final Map<String, Field> fields = new HashMap<>();
  private final Map<String, Field> staticFields = new HashMap<>();

  public FastClasses(Class<T> clazz) {
    this.clazz = clazz;
    if (!clazz.isInterface()) {
      BeanInfo beanInfo = ClassUtil.getBeanInfo(clazz);
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor descriptor : propertyDescriptors) {
        this.properties.put(descriptor.getName(), new Property(descriptor));
      }
      for (Method method : this.clazz.getDeclaredMethods()) {
        Class<?>[] parameters = method.getParameterTypes();
        StringBuilder name = new StringBuilder(method.getName());
        if (parameters.length != 0) {
          for (int i = 0; i < parameters.length; i++) {
            Class<?> parameterType = parameters[i];
            name.append(i == 0 ? "(" : "");
            name.append(parameterType.getName());
            name.append(i + 1 == parameters.length ? ")" : ",");
          }
        } else {
          name.append("()");
        }
        try {
          method.setAccessible(true);
          this.methodProxies.put(name.toString(), new MethodProxy(method, parameters));
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
      for (Constructor<?> constructor : clazz.getConstructors()) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes.length == 1) {
          this.constructors.put(parameterTypes[0], constructor);
        }
      }
    } else {
      for (Method method : clazz.getDeclaredMethods()) {
        StringBuilder name = new StringBuilder(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
          Class<?> parameterType = parameters[i];
          name.append(i == 0 ? "(" : "")
              .append(parameterType.getName())
              .append(i + 1 == parameters.length ? ")" : ",");
        }
        this.methodProxies.put(name.toString(), new MethodProxy(method, parameters));
      }
    }
    for (Class<?> superClass = clazz; superClass != null && superClass != Object.class; ) {
      for (Field field : filterFields(superClass.getDeclaredFields())) {
        if (!this.fields.containsKey(field.getName())) {
          this.fields.put(field.getName(), field);
        }
      }
      superClass = superClass.getSuperclass();
    }
  }

  private List<Field> filterFields(Field[] fields) {
    List<Field> result = new ArrayList<>();
    for (Field field : fields) {
      if (!Modifier.isStatic(field.getModifiers())) {
        field.setAccessible(true);
        result.add(field);
      } else {
        field.setAccessible(true);
        staticFields.put(field.getName(), field);
      }
    }
    return result;
  }

  @Override
  @SneakyThrows({InstantiationException.class, IllegalAccessException.class})
  public T newInstance() {
    return this.clazz.newInstance();
  }

  @Override
  public T newInstance(Object object) {
    if (object == null) {
      return newInstance();
    }
    return newInstance(object.getClass(), object);
  }

  @Override
  @SneakyThrows({
    InstantiationException.class,
    IllegalAccessException.class,
    InvocationTargetException.class
  })
  public T newInstance(Class<?> type, Object object) {
    return (T) this.constructors.get(type).newInstance(object);
  }

  @Override
  public Property getProperty(String name) {
    if (this.properties.containsKey(name)) {
      return this.properties.get(name);
    }
    return null;
  }

  @Override
  public Property[] getProperties() {
    return this.properties.values().toArray(new Property[0]);
  }

  @Override
  public MethodProxy getMethod(String methodName) {
    MethodProxy methodProxy = this.methodProxies.get(methodName + "()");
    if (ObjectUtil.isNotNull(methodProxy)) {
      return methodProxy;
    }
    for (Map.Entry<String, MethodProxy> entry : this.methodProxies.entrySet()) {
      if (entry.getKey().equals(methodName) || entry.getKey().startsWith(methodName + "(")) {
        return entry.getValue();
      }
    }
    if (this.clazz.getSuperclass() != Object.class) {
      return ClassUtil.getMethodProxy(this.clazz.getSuperclass(), methodName);
    }
    return null;
  }

  @Override
  public MethodProxy getMethod(String methodName, Class<?>... parameterTypes) {
    StringBuilder methodNameFull = new StringBuilder(methodName);
    if (parameterTypes.length != 0) {
      for (int i = 0; i < parameterTypes.length; i++) {
        methodNameFull
            .append(i == 0 ? "(" : "")
            .append(parameterTypes[i].getName())
            .append(i + 1 == parameterTypes.length ? ")" : ",");
      }
    } else {
      methodNameFull.append("()");
    }
    return this.methodProxies.get(methodNameFull.toString());
  }

  @Override
  @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
  public void setValue(Object target, String name, Object value) {
    Field field = this.fields.get(name);
    if (field != null) {
      field.set(target, value);
    } else {
      throw new NoSuchFieldException(String.format("没有找到[%s.%s]对应的属性!", clazz.getName(), name));
    }
  }

  @Override
  @SneakyThrows({NoSuchFieldException.class})
  public <V> V getValue(Object target, String name) {
    return getValue(target, this.fields.get(name));
  }

  @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
  public <V> V getValue(Object target, Field field) throws NoSuchFieldException {
    if (field == null) {
      throw new NoSuchFieldException("字段不存在");
    }
    //noinspection unchecked
    return (V) field.get(target);
  }

  @Override
  @SneakyThrows({
    InstantiationException.class,
    IllegalAccessException.class,
    InvocationTargetException.class,
    NoSuchMethodException.class
  })
  public T newInstance(Class<?>[] parameterTypes, Object[] parameters) {
    if (parameterTypes.length == 0) {
      return newInstance();
    }
    if (parameterTypes.length == 1) {
      return newInstance(parameterTypes[0], parameters[0]);
    }
    return clazz.getConstructor(parameterTypes).newInstance(parameters);
  }

  @Override
  public Field[] getDeclaredFields() {
    return this.fields.values().toArray(new Field[0]);
  }

  @Override
  public Field getDeclaredField(String fieldName) {
    return this.fields.get(fieldName);
  }

  @Override
  public Field[] getDeclaredFields(Class<? extends Annotation> annotClass) {
    List<Field> retValues = new ArrayList<>();
    for (Field field : this.fields.values()) {
      if (field.isAnnotationPresent(annotClass)) {
        retValues.add(field);
      }
    }
    return retValues.toArray(new Field[0]);
  }

  @Override
  @SneakyThrows({NoSuchFieldException.class})
  public <V> V getValue(String name) {
    return this.getValue(null, this.staticFields.get(name));
  }
}
