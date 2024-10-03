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
import net.asany.jfantasy.framework.log.annotation.LogOperationSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

abstract class LogOperationSourcePointcut extends StaticMethodMatcherPointcut
    implements Serializable {

  @Override
  public boolean matches(@NotNull Method method, @NotNull Class<?> targetClass) {
    LogOperationSource cas = getLogOperationSource();
    return cas != null && !CollectionUtils.isEmpty(cas.getOperations(method, targetClass));
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof LogOperationSourcePointcut otherPc)) {
      return false;
    }
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
