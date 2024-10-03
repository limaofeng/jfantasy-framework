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
package net.asany.jfantasy.framework.dao.hibernate;

import java.util.Map;
import net.asany.jfantasy.framework.dao.jpa.SimpleAnyJpaRepository;
import org.hibernate.Interceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

public class InterceptorRegistration implements HibernatePropertiesCustomizer {

  private final Interceptor interceptor;

  public InterceptorRegistration(Interceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    if (!hibernateProperties.containsKey("hibernate.jdbc.batch_size")) {
      hibernateProperties.put("hibernate.jdbc.batch_size", SimpleAnyJpaRepository.BATCH_SIZE);
    } else {
      SimpleAnyJpaRepository.BATCH_SIZE =
          Integer.parseInt((String) hibernateProperties.get("hibernate.jdbc.batch_size"));
    }
    hibernateProperties.put("hibernate.order_inserts", true);
    hibernateProperties.put("hibernate.order_updates", true);
    hibernateProperties.put("hibernate.jdbc.batch_versioned_data", true);
    hibernateProperties.put("hibernate.session_factory.interceptor", this.interceptor);
    //    hibernateProperties.put(
    //        "hibernate.ejb.identifier_generator_strategy_provider",
    //        new CustomIdentifierGeneratorStrategyProvider());
  }
}
