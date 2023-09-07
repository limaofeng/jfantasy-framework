package org.jfantasy.framework.dao.hibernate.generator;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.hibernate.annotations.SerialNumberGenerator;
import org.jfantasy.framework.spring.SpELUtil;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class DefaultSerialNumberGenerator implements IdentifierGenerator {

  private final SpelExpressionParser parser = new SpelExpressionParser();
  private Expression expression;

  public DefaultSerialNumberGenerator() {}

  public DefaultSerialNumberGenerator(
      SerialNumberGenerator serialNumberGenerator,
      Member ignoredMember,
      CustomIdGeneratorCreationContext generatorCreationContext) {
    this();
    this.expression = parser.parseExpression(serialNumberGenerator.value());
  }

  @Override
  public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
    this.expression = parser.parseExpression(params.getProperty("expression"));
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    return expression.getValue(SpELUtil.createEvaluationContext(object), String.class);
  }
}
