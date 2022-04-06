package org.jfantasy.framework.log.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.jfantasy.framework.log.annotation.LogOperationSource;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

abstract class LogOperationSourcePointcut extends StaticMethodMatcherPointcut
    implements Serializable {

  @Override
  public boolean matches(Method method, Class<?> targetClass) {
    LogOperationSource cas = getLogOperationSource();
    return cas != null && !CollectionUtils.isEmpty(cas.getOperations(method, targetClass));
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof LogOperationSourcePointcut)) {
      return false;
    }
    LogOperationSourcePointcut otherPc = (LogOperationSourcePointcut) other;
    return ObjectUtils.nullSafeEquals(getLogOperationSource(), otherPc.getLogOperationSource());
  }

  @Override
  public int hashCode() {
    return LogOperationSourcePointcut.class.hashCode();
  }

  @Override
  public String toString() {
    return getClass().getName() + ": " + getLogOperationSource();
  }

  protected abstract LogOperationSource getLogOperationSource();
}
