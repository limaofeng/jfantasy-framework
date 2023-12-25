package net.asany.jfantasy.framework.log.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class LogInterceptor extends LogAspectSupport implements MethodInterceptor, Serializable {

  private static class ThrowableWrapper extends RuntimeException {
    private final Throwable original;

    ThrowableWrapper(Throwable original) {
      this.original = original;
    }
  }

  @Override
  public Object invoke(final MethodInvocation invocation) throws Throwable {
    Method method = invocation.getMethod();
    Invoker aopAllianceInvoker =
        () -> {
          try {
            return invocation.proceed();
          } catch (Throwable ex) {
            log.debug(ex.getMessage(), ex);
            throw new ThrowableWrapper(ex);
          }
        };
    try {
      return execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
    } catch (ThrowableWrapper th) {
      log.error(th.getMessage(), th);
      throw th.original;
    }
  }
}
