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
package net.asany.jfantasy.framework.security.auth.core;

import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class TokenServicesFactory implements BeanDefinitionRegistryPostProcessor {

  private ConfigurableListableBeanFactory beanFactory;
  private final Map<
          Class<? extends Authentication>, Class<? extends AuthorizationServerTokenServices<?>>>
      tokenServicesMap = new HashMap<>();

  public <T> T getTokenServices(Class<? extends Authentication> type) {
    Class<?> serviceClass = tokenServicesMap.get(type);
    if (serviceClass != null) {
      return (T) beanFactory.getBean(serviceClass);
    }
    throw new IllegalArgumentException("No token service registered for type: " + type);
  }

  public void registerTokenService(
      Class<? extends Authentication> authenticationClass,
      Class<? extends AuthorizationServerTokenServices<?>> tokenServicesClass) {
    tokenServicesMap.put(authenticationClass, tokenServicesClass);
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
