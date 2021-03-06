package org.jfantasy.framework.log.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

class LazyParamAwareEvaluationContext extends StandardEvaluationContext {

  private final ParameterNameDiscoverer paramDiscoverer;

  private final Method method;

  private final Object[] args;

  private final Class<?> targetClass;

  private final Map<String, Method> methodCache;

  private boolean paramLoaded = false;

  LazyParamAwareEvaluationContext(
      Object rootObject,
      ParameterNameDiscoverer paramDiscoverer,
      Method method,
      Object[] args,
      Class<?> targetClass,
      Map<String, Method> methodCache) {
    super(rootObject);

    this.paramDiscoverer = paramDiscoverer;
    this.method = method;
    this.args = args;
    this.targetClass = targetClass;
    this.methodCache = methodCache;
  }

  /** Load the param information only when needed. */
  @Override
  public Object lookupVariable(String name) {
    Object variable = super.lookupVariable(name);
    if (variable != null) {
      return variable;
    }
    if (!this.paramLoaded) {
      loadArgsAsVariables();
      this.paramLoaded = true;
      variable = super.lookupVariable(name);
    }
    return variable;
  }

  private void loadArgsAsVariables() {
    // shortcut if no args need to be loaded
    if (ObjectUtils.isEmpty(this.args)) {
      return;
    }

    String mKey = toString(this.method);
    Method targetMethod = this.methodCache.get(mKey);
    final boolean nullTargetMethod = targetMethod == null;
    if (nullTargetMethod) {
      targetMethod =
          ObjectUtil.defaultValue(
              AopUtils.getMostSpecificMethod(this.method, this.targetClass), this.method);
      this.methodCache.put(mKey, targetMethod);
    }

    // save arguments as indexed variables
    for (int i = 0; i < this.args.length; i++) {
      setVariable("a" + i, this.args[i]);
      setVariable("p" + i, this.args[i]);
    }

    String[] parameterNames = this.paramDiscoverer.getParameterNames(targetMethod);
    // save parameter names (if discovered)
    if (parameterNames != null) {
      for (int i = 0; i < parameterNames.length; i++) {
        setVariable(parameterNames[i], this.args[i]);
      }
    }
  }

  private String toString(Method m) {
    return m.getDeclaringClass().getName() + "#" + m;
  }
}
