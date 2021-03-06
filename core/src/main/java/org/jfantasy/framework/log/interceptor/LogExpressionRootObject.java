package org.jfantasy.framework.log.interceptor;

import java.lang.reflect.Method;
import org.springframework.util.Assert;

public class LogExpressionRootObject {

  private final Method method;

  private final Object[] args;

  private final Object target;

  private final Class<?> targetClass;

  public LogExpressionRootObject(
      Method method, Object[] args, Object target, Class<?> targetClass) {
    Assert.notNull(method, "Method is required");
    Assert.notNull(targetClass, "targetClass is required");
    this.method = method;
    this.target = target;
    this.targetClass = targetClass;
    this.args = args;
  }

  public Method getMethod() {
    return this.method;
  }

  public String getMethodName() {
    return this.method.getName();
  }

  public Object[] getArgs() {
    return this.args;
  }

  public Object getTarget() {
    return this.target;
  }

  public Class getTargetClass() {
    return this.targetClass;
  }
}
