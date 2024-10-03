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
package net.asany.jfantasy.framework.spring;

import java.util.Map;
import net.asany.jfantasy.framework.dao.mybatis.keygen.util.DatabaseSequenceGenerator;
import net.asany.jfantasy.framework.util.common.DateUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELUtil {

  private SpELUtil() {}

  public static EvaluationContext createEvaluationContext() {
    return initialVariable(new StandardEvaluationContext());
  }

  public static EvaluationContext createEvaluationContext(Object object) {
    return initialVariable(new StandardEvaluationContext(object));
  }

  public static EvaluationContext createEvaluationContext(Map<String, Object> data) {
    EvaluationContext context = createEvaluationContext();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      context.setVariable(entry.getKey(), entry.getValue());
    }
    return context;
  }

  public static EvaluationContext createEvaluationContext(Object object, Map<String, Object> data) {
    EvaluationContext context = createEvaluationContext(object);
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      context.setVariable(entry.getKey(), entry.getValue());
    }
    return context;
  }

  public static Expression getExpression(String el) {
    SpelExpressionParser parser = new SpelExpressionParser();
    return parser.parseExpression(el);
  }

  private static EvaluationContext initialVariable(EvaluationContext context) {
    context.setVariable("DateUtil", DateUtil.class);
    context.setVariable("SequenceInfo", DatabaseSequenceGenerator.class);
    context.setVariable("StringUtil", StringUtil.class);
    context.setVariable("systemProperties", System.getenv());
    return context;
  }
}
