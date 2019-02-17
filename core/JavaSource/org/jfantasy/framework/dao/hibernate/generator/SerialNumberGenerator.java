package org.jfantasy.framework.dao.hibernate.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.jfantasy.framework.spring.SpELUtil;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.Serializable;
import java.util.Properties;

public class SerialNumberGenerator implements IdentifierGenerator, Configurable {

	private SpelExpressionParser parser = new SpelExpressionParser();
	private Expression expression;

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
		this.expression = parser.parseExpression(params.getProperty("expression"));
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return expression.getValue(SpELUtil.createEvaluationContext(object), String.class);
	}
}
