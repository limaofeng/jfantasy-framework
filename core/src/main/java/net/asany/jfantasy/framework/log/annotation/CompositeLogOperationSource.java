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
package net.asany.jfantasy.framework.log.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.util.Assert;

@SuppressWarnings("serial")
public class CompositeLogOperationSource implements LogOperationSource, Serializable {

  private final transient LogOperationSource[] logOperationSources;

  public CompositeLogOperationSource(LogOperationSource... logOperationSources) {
    Assert.notEmpty(logOperationSources, "logOperationSources array must not be empty");
    this.logOperationSources = logOperationSources;
  }

  public final LogOperationSource[] getLogOperationSources() {
    return this.logOperationSources;
  }

  @Override
  public Collection<LogOperation> getOperations(Method method, Class<?> targetClass) {
    Collection<LogOperation> ops = null;
    for (LogOperationSource source : this.logOperationSources) {
      Collection<LogOperation> logOperations = source.getOperations(method, targetClass);
      if (logOperations != null) {
        if (ops == null) {
          ops = new ArrayList<>();
        }
        ops.addAll(logOperations);
      }
    }
    return ops;
  }
}
