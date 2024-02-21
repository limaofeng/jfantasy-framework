package net.asany.jfantasy.framework.security.auth.core;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TokenServiceFactory
    implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

  private ApplicationContext applicationContext;

  private final Map<Class<? extends AuthToken>, Class<?>> tokenServicesMap = new HashMap<>();
  private ConfigurableListableBeanFactory beanFactory;

  public <T> T getTokenServices(Class<? extends AuthToken> type) {
    Class<?> serviceClass = tokenServicesMap.get(type);
    if (serviceClass != null) {
      //noinspection unchecked
      return (T) beanFactory.getBean(serviceClass);
    }
    throw new IllegalArgumentException("No token service registered for type: " + type);
  }

  public void registerTokenService(Class<? extends AuthToken> tokenType, Class<?> serviceClass) {
    tokenServicesMap.put(tokenType, serviceClass);
  }

  @Override
  public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry)
      throws BeansException {
    tokenServicesMap.forEach(
        (tokenType, serviceClass) -> {
          BeanDefinition beanDefinition = buildBeanDefinition(serviceClass);
          registry.registerBeanDefinition(serviceClass.getSimpleName(), beanDefinition);
        });
  }

  private BeanDefinition buildBeanDefinition(Class<?> serviceClass) {
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(serviceClass);

    //    Constructor<?>[] constructors = serviceClass.getConstructors();
    //    if (constructors.length > 0) {
    //      Constructor<?> constructor = constructors[0];
    //
    //      for (Class<?> paramType : constructor.getParameterTypes()) {
    //        // 参数是其他bean，添加bean引用
    //        String beanName = applicationContext.getBeanNamesForType(paramType)[0];
    //        builder.addConstructorArgReference(beanName);
    //      }
    //    }

    return builder.getBeanDefinition();
  }

  @Override
  public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }
}
