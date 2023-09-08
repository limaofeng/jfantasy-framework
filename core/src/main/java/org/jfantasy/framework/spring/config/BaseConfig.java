package org.jfantasy.framework.spring.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class BaseConfig {

  public static Validator getValidator(ApplicationContext applicationContext) {
    if (applicationContext instanceof XmlWebApplicationContext) {
      ConfigurableApplicationContext configurableApplicationContext =
          (ConfigurableApplicationContext) applicationContext;
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
