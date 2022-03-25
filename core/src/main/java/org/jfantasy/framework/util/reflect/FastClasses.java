package org.jfantasy.framework.util.reflect;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.cglib.reflect.FastClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;

public class FastClasses<T> implements IClass<T> {
  private static final Log LOGGER = LogFactory.getLog(FastClasses.class);
  private final Class<T> clazz;
  private final Map<String, Property> properties = new HashMap<>();
  private final Map<String, MethodProxy> methodProxies = new HashMap<>();
  private final Map<Class<?>, Constructor<T>> constructors = new HashMap<>();
  private final Map<String, Field> fields = new HashMap<>();
  private final Map<String, Field> staticFields = new HashMap<>();

  public FastClasses(Class<T> clazz) {
    this.clazz = clazz;
    FastClass fastClass = FastClass.create(clazz);
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
          if (method.isAccessible()) {
            this.methodProxies.put(
                name.toString(), new MethodProxy(fastClass.getMethod(method), parameters));
          } else {
            if (!method.isAccessible()) {
              method.setAccessible(true);
            }
            this.methodProxies.put(name.toString(), new MethodProxy(method, parameters));
          }
        } catch (Exception e) {
          LOGGER.error(e.getMessage(), e);
        }
      }
      for (Constructor<?> constructor : clazz.getConstructors()) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes.length == 1) {
          this.constructors.put(parameterTypes[0], (Constructor<T>) constructor);
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
        this.methodProxies.put(
            name.toString(), new MethodProxy(fastClass.getMethod(method), parameters));
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
  public T newInstance() {
    try {
      return this.clazz.newInstance();
    } catch (Exception e) {
      throw new IgnoreException(e.getMessage(), e);
    }
  }

  @Override
  public T newInstance(Object object) {
    try {
      if (object == null) {
        return newInstance();
      }
      return newInstance(object.getClass(), object);
    } catch (Exception e) {
      throw new IgnoreException(e.getMessage(), e);
    }
  }

  @Override
  public T newInstance(Class<?> type, Object object) {
    try {
      return this.constructors.get(type).newInstance(object);
    } catch (Exception e) {
      throw new IgnoreException(e.getMessage(), e);
    }
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
    StringBuilder methodname = new StringBuilder(methodName);
    if (parameterTypes.length != 0) {
      for (int i = 0; i < parameterTypes.length; i++) {
        methodname
            .append(i == 0 ? "(" : "")
            .append(parameterTypes[i].getName())
            .append(i + 1 == parameterTypes.length ? ")" : ",");
      }
    } else {
      methodname.append("()");
    }
    return this.methodProxies.get(methodname.toString());
  }

  @Override
  public void setValue(Object target, String name, Object value) {
    Field field = this.fields.get(name);
    try {
      if (field != null) {
        field.set(target, value);
      } else {
        throw new IgnoreException(String.format("没有找到[%s.%s]对应的属性!", clazz.getName(), name));
      }
    } catch (Exception e) {
      throw new IgnoreException(e.getMessage(), e);
    }
  }

  @Override
  public <V> V getValue(Object target, String name) {
    try {
      return getValue(target, this.fields.get(name));
    } catch (IllegalAccessException | NoSuchFieldException ex) {
      throw new IgnoreException(String.format("没有找到[%s.%s]对应的属性!", clazz.getName(), name), ex);
    }
  }

  @SuppressWarnings({"rawtypes"})
  public <V> V getValue(Object target, Field field)
      throws IllegalAccessException, NoSuchFieldException {
    if (field == null) {
      throw new NoSuchFieldException("字段不存在");
    }
    return (V) field.get(target);
  }

  @Override
  public T newInstance(Class<?>[] parameterTypes, Object[] parameters) {
    if (parameterTypes.length == 0) {
      return newInstance();
    }
    if (parameterTypes.length == 1) {
      return newInstance(parameterTypes[0], parameters[0]);
    }
    throw new IgnoreException("还不支持多个参数的构造器");
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
  public <V> V getValue(String name) {
    try {
      return this.getValue(null, this.staticFields.get(name));
    } catch (IllegalAccessException | NoSuchFieldException ex) {
      throw new IgnoreException(String.format("没有找到[%s.%s]对应的属性!", clazz.getName(), name), ex);
    }
  }
}
