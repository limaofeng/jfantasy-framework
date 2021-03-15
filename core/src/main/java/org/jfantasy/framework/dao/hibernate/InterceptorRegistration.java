package org.jfantasy.framework.dao.hibernate;

import org.hibernate.Interceptor;
import org.jfantasy.framework.dao.hibernate.spi.CustomIdentifierGeneratorStrategyProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

import java.util.Map;

public class InterceptorRegistration implements HibernatePropertiesCustomizer {

    private Interceptor interceptor;

    public InterceptorRegistration(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", this.interceptor);
        hibernateProperties.put("hibernate.ejb.identifier_generator_strategy_provider", new CustomIdentifierGeneratorStrategyProvider());
    }
}
