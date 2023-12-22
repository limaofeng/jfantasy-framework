package org.jfantasy.framework.log.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

@Slf4j
public abstract class AbstractFallbackLogOperationSource implements LogOperationSource {

  private static final Collection<LogOperation> NULL_ATTRIBUTE = Collections.emptyList();

  final Map<Object, Collection<LogOperation>> attributeCache = new ConcurrentHashMap<>();

  @Override
  public Collection<LogOperation> getOperations(Method method, Class<?> targetClass) {
    Object cacheKey = getCacheKey(method, targetClass);
    Collection<LogOperation> logOperations = this.attributeCache.get(cacheKey);
    if (logOperations != null) {
      if (logOperations == NULL_ATTRIBUTE) {
        return null;
      }
      return logOperations;
    } else {
      Collection<LogOperation> logOps = computeLogOperations(method, targetClass);
      if (logOps == null) {
        this.attributeCache.put(cacheKey, NULL_ATTRIBUTE);
      } else {
        if (log.isDebugEnabled()) {
          log.debug("Adding cacheable method '" + method.getName() + "' with attribute: " + logOps);
        }
        this.attributeCache.put(cacheKey, logOps);
      }
      return logOps;
    }
  }

  protected Object getCacheKey(Method method, Class<?> targetClass) {
    return new DefaultCacheKey(method, targetClass);
  }

  private Collection<LogOperation> computeLogOperations(Method method, Class<?> targetClass) {
    if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
      return null;
    }
    Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
    specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
    Collection<LogOperation> opDef = findOperations(specificMethod);
    if (opDef != null) {
      return opDef;
    }
    opDef = findOperations(specificMethod.getDeclaringClass());
    if (opDef != null) {
      return opDef;
    }
    if (specificMethod != method) {
      opDef = findOperations(method);
      if (opDef != null) {
        return opDef;
      }
      return findOperations(method.getDeclaringClass());
    }
    return null;
  }

  protected abstract Collection<LogOperation> findOperations(Method method);

  protected abstract Collection<LogOperation> findOperations(Class<?> clazz);

  protected boolean allowPublicMethodsOnly() {
    return false;
  }

  private static class DefaultCacheKey {

    private final Method method;

    private final Class<?> targetClass;

    public DefaultCacheKey(Method method, Class<?> targetClass) {
      this.method = method;
      this.targetClass = targetClass;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof DefaultCacheKey)) {
        return false;
      }
      DefaultCacheKey otherKey = (DefaultCacheKey) other;
      return this.method.equals(otherKey.method)
          && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass);
    }

    @Override
    public int hashCode() {
      return this.method.hashCode() * 29
          + (this.targetClass != null ? this.targetClass.hashCode() : 0);
    }
  }
}
