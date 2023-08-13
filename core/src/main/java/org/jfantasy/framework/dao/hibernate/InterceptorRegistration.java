package org.jfantasy.framework.dao.hibernate;

import java.util.Map;
import org.hibernate.Interceptor;
import org.jfantasy.framework.dao.hibernate.spi.CustomIdentifierGeneratorStrategyProvider;
import org.jfantasy.framework.dao.jpa.ComplexJpaRepository;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

public class InterceptorRegistration implements HibernatePropertiesCustomizer {

  private final Interceptor interceptor;

  public InterceptorRegistration(Interceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    if (!hibernateProperties.containsKey("hibernate.jdbc.batch_size")) {
      hibernateProperties.put("hibernate.jdbc.batch_size", ComplexJpaRepository.BATCH_SIZE);
    } else {
      ComplexJpaRepository.BATCH_SIZE =
          Integer.parseInt((String) hibernateProperties.get("hibernate.jdbc.batch_size"));
    }
    hibernateProperties.put("hibernate.order_inserts", true);
    hibernateProperties.put("hibernate.order_updates", true);
    hibernateProperties.put("hibernate.jdbc.batch_versioned_data", true);
    hibernateProperties.put("hibernate.session_factory.interceptor", this.interceptor);
    hibernateProperties.put(
        "hibernate.ejb.identifier_generator_strategy_provider",
        new CustomIdentifierGeneratorStrategyProvider());
  }
}
