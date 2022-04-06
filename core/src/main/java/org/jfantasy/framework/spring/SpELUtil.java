package org.jfantasy.framework.spring;

import java.util.Map;
import org.jfantasy.framework.dao.mybatis.keygen.util.SequenceInfo;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;
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
    context.setVariable("SequenceInfo", SequenceInfo.class);
    context.setVariable("StringUtil", StringUtil.class);
    context.setVariable("systemProperties", System.getenv());
    return context;
  }
}
