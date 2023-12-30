package net.asany.jfantasy.framework.log.interceptor;

import java.lang.reflect.Method;
import org.springframework.util.Assert;

public record LogExpressionRootObject(
    Method method, Object[] args, Object target, Class<?> targetClass) {

  public LogExpressionRootObject {
    Assert.notNull(method, "Method is required");
    Assert.notNull(targetClass, "targetClass is required");
  }

  public String getMethodName() {
    return this.method.getName();
  }
}
