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
