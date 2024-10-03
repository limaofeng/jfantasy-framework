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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ExpressionEvaluator {

  private final SpelExpressionParser parser = new SpelExpressionParser();

  // TODO: LocalVariableTableParameterNameDiscoverer 改为 DefaultParameterNameDiscoverer 待测试
  private final ParameterNameDiscoverer paramNameDiscoverer = new DefaultParameterNameDiscoverer();

  private final Map<String, Expression> conditionCache = new ConcurrentHashMap<>();

  private final Map<String, Expression> keyCache = new ConcurrentHashMap<>();

  private final Map<String, Method> targetMethodCache = new ConcurrentHashMap<>();

  public EvaluationContext createEvaluationContext(
      Method method, Object[] args, Object target, Class<?> targetClass) {

    LogExpressionRootObject rootObject =
        new LogExpressionRootObject(method, args, target, targetClass);
    return new LazyParamAwareEvaluationContext(
        rootObject, this.paramNameDiscoverer, method, args, targetClass, this.targetMethodCache);
  }

  public boolean condition(
      String conditionExpression, Method method, EvaluationContext evalContext) {
    String key = toString(method, conditionExpression);
    Expression condExp = this.conditionCache.get(key);
    if (condExp == null) {
      condExp = this.parser.parseExpression(conditionExpression);
      this.conditionCache.put(key, condExp);
    }
    return Boolean.TRUE.equals(condExp.getValue(evalContext, boolean.class));
  }

  public Object key(String keyExpression, Method method, EvaluationContext evalContext) {
    String key = toString(method, keyExpression);
    Expression keyExp = this.keyCache.get(key);
    if (keyExp == null) {
      keyExp = this.parser.parseExpression(keyExpression);
      this.keyCache.put(key, keyExp);
    }
    return keyExp.getValue(evalContext);
  }

  private String toString(Method method, String expression) {
    return method.getDeclaringClass().getName() + "#" + method + "#" + expression;
  }
}
