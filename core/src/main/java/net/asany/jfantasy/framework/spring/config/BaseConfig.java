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
package net.asany.jfantasy.framework.spring.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class BaseConfig {

  public static Validator getValidator(ApplicationContext applicationContext) {
    if (applicationContext instanceof XmlWebApplicationContext configurableApplicationContext) {
      DefaultListableBeanFactory defaultListableBeanFactory =
          (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
      BeanDefinitionBuilder beanDefinitionBuilder =
          BeanDefinitionBuilder.genericBeanDefinition(LocalValidatorFactoryBean.class);
      beanDefinitionBuilder.setAutowireMode(2);
      beanDefinitionBuilder.addPropertyValue("providerClass", HibernateValidator.class);
      defaultListableBeanFactory.registerBeanDefinition(
          "validator", beanDefinitionBuilder.getBeanDefinition());
      return configurableApplicationContext.getBean("validator", Validator.class);
    }
    return null;
  }
}
