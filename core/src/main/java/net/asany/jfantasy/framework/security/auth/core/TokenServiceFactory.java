package net.asany.jfantasy.framework.security.auth.core;

import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class TokenServiceFactory implements BeanDefinitionRegistryPostProcessor {

  private ConfigurableListableBeanFactory beanFactory;
  private final Map<
          Class<? extends AuthToken>,
          Class<? extends AuthorizationServerTokenServices<? extends AuthToken>>>
      tokenServicesMap = new HashMap<>();

  public <T> T getTokenServices(Class<? extends AuthToken> type) {
    Class<?> serviceClass = tokenServicesMap.get(type);
    if (serviceClass != null) {
      //noinspection unchecked
      return (T) beanFactory.getBean(serviceClass);
    }
    throw new IllegalArgumentException("No token service registered for type: " + type);
  }

  public void registerTokenService(
      Class<? extends AuthToken> tokenType,
      Class<? extends AuthorizationServerTokenServices<? extends AuthToken>> serviceClass) {
    tokenServicesMap.put(tokenType, serviceClass);
  }

  @Override
  public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry)
      throws BeansException {
    tokenServicesMap.forEach(
        (tokenType, serviceClass) -> {
          if (!hasBeanDefinition(registry, serviceClass)) {
            BeanDefinition beanDefinition = buildBeanDefinition(serviceClass);
            registry.registerBeanDefinition(serviceClass.getSimpleName(), beanDefinition);
          }
        });
  }

  private boolean hasBeanDefinition(
      BeanDefinitionRegistry registry,
      Class<? extends AuthorizationServerTokenServices<? extends AuthToken>> serviceClass) {
    try {
      ((DefaultListableBeanFactory) registry).getBean(serviceClass);
      return true;
    } catch (BeansException e) {
      return false;
    }
  }

  private BeanDefinition buildBeanDefinition(Class<?> serviceClass) {
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(serviceClass);
    return builder.getBeanDefinition();
  }

  @Override
  public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    this.beanFactory = beanFactory;
  }
}
