package org.jfantasy.framework.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastMethod;
import org.jfantasy.framework.util.common.JavassistUtil;

@Slf4j
public class MethodProxy {

  private final Object method;
  private Class<?>[] parameterTypes;
  private final Class<?> returnType;
  private final Class<?> declaringClass;

  public MethodProxy(Object method) {
    this.method = method;
    if (method instanceof FastMethod) {
      this.parameterTypes = ((FastMethod) method).getParameterTypes();
      this.returnType = ((FastMethod) method).getReturnType();
      this.declaringClass = ((FastMethod) method).getDeclaringClass();
    } else {
      this.parameterTypes = ((Method) method).getParameterTypes();
      this.returnType = ((Method) method).getReturnType();
      this.declaringClass = ((Method) method).getDeclaringClass();
    }
  }

  public MethodProxy(Object method, Class<?>[] parameterTypes) {
    this(method);
    this.parameterTypes = parameterTypes;
  }

  public MethodProxy(Object method, Class<?> parameterType) {
    this(method);
    if (parameterType != null) {
      this.parameterTypes = new Class[] {parameterType};
    }
  }

  public Object invoke(Object object, Object param) {
    return invoke(object, new Object[] {param});
  }

  public Object invoke(Object object, Object... params) {
    try {
      if (this.method instanceof FastMethod) {
        if (params.length > 0) {
          return ((FastMethod) this.method).invoke(object, params);
        }
        return ((FastMethod) this.method).invoke(object, params);
      }
      if (params.length > 0) {
        return ((Method) this.method).invoke(object, params);
      }
      return ((Method) this.method).invoke(object, params);
    } catch (IllegalAccessException | InvocationTargetException | RuntimeException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public static MethodProxy create(Object method) {
    if (method == null) {
      return null;
    }
    return new MethodProxy(method);
  }

  public Class[] getParameterTypes() {
    return this.parameterTypes;
  }

  public Class getReturnType() {
    return this.returnType;
  }

  @Override
  public String toString() {
    return "MethodProxy [" + this.method.toString() + "]";
  }

  public Method getMethod() {
    return (this.method instanceof FastMethod)
        ? ((FastMethod) this.method).getJavaMethod()
        : (Method) this.method;
  }

  public Annotation[] getAnnotations() {
    return getMethod().getAnnotations();
  }

  public <T extends Annotation> T getAnnotation(Class<T> tClass) {
    return getMethod().getAnnotation(tClass);
  }

  public String[] getParamNames() {
    try {
      if (this.method instanceof FastMethod) {
        Class dclass = ((FastMethod) this.method).getDeclaringClass();
        return JavassistUtil.getParamNames(
            dclass.getName(), ((FastMethod) this.method).getName(), this.parameterTypes);
      }
      Class dclass = ((Method) this.method).getDeclaringClass();
      return JavassistUtil.getParamNames(
          dclass.getName(), ((Method) this.method).getName(), this.parameterTypes);
    } catch (NotFoundException | JavassistUtil.MissingLVException e) {
      log.error(e.getMessage(), e);
      return new String[0];
    }
  }

  public Class getDeclaringClass() {
    return this.declaringClass;
  }
}
