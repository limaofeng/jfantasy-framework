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
