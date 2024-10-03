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
package net.asany.jfantasy.framework.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

@Slf4j
public class SpringBeanUtils
    implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

  /** Spring应用上下文环境 -- GETTER -- 获取 spring 上下文 */
  @Getter private static ApplicationContext applicationContext;

  private static BeanDefinitionRegistry registry;
  private static ConfigurableListableBeanFactory beanFactory;

  public static void addApplicationListener(ApplicationListener<?> listener) {
    if (applicationContext instanceof ConfigurableApplicationContext) {
      ((ConfigurableApplicationContext) applicationContext).addApplicationListener(listener);
    } else {
      log.warn("applicationContext is not instanceof ConfigurableApplicationContext");
    }
  }

  @Override
  public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry)
      throws BeansException {
    SpringBeanUtils.registry = registry;
  }

  @Override
  public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    SpringBeanUtils.beanFactory = beanFactory;
  }

  /**
   * 实现ApplicationContextAware接口的回调方法，设置上下文环境
   *
   * @param applicationContext applicationContext
   */
  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
    if (ObjectUtil.isNull(SpringBeanUtils.applicationContext)) {
      SpringBeanUtils.applicationContext = applicationContext;
    }
  }

  public static void setRegistry(BeanDefinitionRegistry registry) {
    SpringBeanUtils.registry = registry;
  }

  @Getter
  public enum AutoType {
    //  自动装配
    AUTOWIRE_NO(0),
    // 根据名称自动装配
    AUTOWIRE_BY_NAME(1),
    // 根据类型自动装配
    AUTOWIRE_BY_TYPE(2),
    // 构造函数自动装配
    AUTOWIRE_CONSTRUCTOR(3),
    // 自动检测
    AUTOWIRE_AUTODETECT(4);

    private final int value;

    AutoType(int value) {
      this.value = value;
    }
  }

  /**
   * 获取对象
   *
   * @param name beanId
   * @return Object 一个以所给名字注册的bean的实例
   */
  public static synchronized <T> T getBean(String name) {
    try {
      return (T) applicationContext.getBean(name);
    } catch (NoSuchBeanDefinitionException e) {
      if (log.isErrorEnabled()) {
        log.error("BeanName:{}没有找到!", name, e);
      }
      return null;
    } catch (BeansException e) {
      if (log.isErrorEnabled()) {
        log.error("BeanName:{}没有找到!", name, e);
      }
      throw e;
    }
  }

  /**
   * 获取类型为requiredType的对象 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
   *
   * @param name bean注册名
   * @param requiredType 返回对象类型
   * @return Object 返回requiredType类型对象
   */
  public static synchronized <T> T getBean(String name, Class<T> requiredType) {
    try {
      return applicationContext.getBean(name, requiredType);
    } catch (NoSuchBeanDefinitionException e) {
      if (log.isErrorEnabled()) {
        log.error("{Bean:" + name + ",Class:" + requiredType + "}没有找到!", e);
      }
      return null;
    } catch (BeansException e) {
      if (log.isErrorEnabled()) {
        log.error("{Bean:" + name + ",Class:" + requiredType + "}没有找到!", e);
      }
      throw e;
    } catch (NullPointerException e) {
      log.error("查找Bean:{}时发现applicationContext未启动", name, e);
      return null;
    }
  }

  public static synchronized <T> T autowireBean(T existingBean) {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(existingBean);
    return existingBean;
  }

  /**
   * 由spring容器初始化该对象
   *
   * @param <T> 泛型
   * @param beanClass 泛型 class
   * @param autoType 自动注入方式
   * @return T 对象
   * @see AutoType
   */
  public static synchronized <T> T autowire(Class<T> beanClass, AutoType autoType) {
    return (T)
        applicationContext
            .getAutowireCapableBeanFactory()
            .autowire(beanClass, autoType.getValue(), false);
  }

  /**
   * spring 创建该Bean
   *
   * @param <T> 泛型
   * @param beanClass 泛型 class
   * @param autoType 自动注入方式
   * @return T 对象
   * @see AutoType
   */
  public static synchronized <T> T createBean(Class<T> beanClass, AutoType autoType) {
    return (T)
        applicationContext
            .getAutowireCapableBeanFactory()
            .createBean(beanClass, autoType.getValue(), false);
  }

  /**
   * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
   *
   * @param name Bean Name
   * @return boolean
   */
  public static synchronized boolean containsBean(String name) {
    return applicationContext.containsBean(name);
  }

  /**
   * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
   * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
   *
   * @param name BeanName
   * @return boolean
   */
  public static synchronized boolean isSingleton(String name) {
    return applicationContext.isSingleton(name);
  }

  /**
   * 获取注册对象的类型
   *
   * @param name beanName
   * @return Class 注册对象的类型
   */
  public static synchronized <T> Class<T> getType(String name) {
    return (Class<T>) applicationContext.getType(name);
  }

  /**
   * 如果给定的bean名字在bean定义中有别名，则返回这些别名
   *
   * @param name beanName
   * @return name aliases
   */
  public static synchronized String[] getAliases(String name) {
    return applicationContext.getAliases(name);
  }

  public static synchronized <T> Map<String, T> getBeansOfType(Class<T> clazz) {
    return applicationContext.getBeansOfType(clazz);
  }

  public static synchronized <T> String[] getBeanNamesForType(Class<T> clazz) {
    return applicationContext.getBeanNamesForType(clazz);
  }

  public static <T> boolean containsBean(Class<T> clazz) {
    return applicationContext != null && getBeanNamesForType(clazz).length > 0;
  }

  public static <T> T getBean(Class<T> clazz) {
    return getBeanByType(clazz);
  }

  public static <T> T getBeanByType(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }

  public static synchronized Resource[] getResources(String pattern) {
    try {
      return applicationContext.getResources(pattern);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return new Resource[0];
    }
  }

  public static boolean startup() {
    return applicationContext != null;
  }

  public void destroy() {
    cleanApplicationContext();
  }

  public void publishEvent(Object event) {
    getApplicationContext().publishEvent(event);
  }

  public static void cleanApplicationContext() {
    applicationContext = null;
  }

  public static synchronized <T> T registerBeanDefinition(String beanName, Class<?> clazz) {
    BeanDefinitionBuilder beanDefinitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(clazz);
    beanDefinitionBuilder.setAutowireMode(AutoType.AUTOWIRE_BY_TYPE.getValue());
    registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    return getBean(beanName);
  }

  public static <T> T registerBeanDefinition(String beanName, Class<?> clazz, Object[] argValues) {
    return registerBeanDefinition(beanName, clazz, argValues, new HashMap<>());
  }

  public static <T> T registerBeanDefinition(
      String beanName, Class<?> clazz, Map<String, Object> propertyValues) {
    return registerBeanDefinition(beanName, clazz, new Object[0], propertyValues);
  }

  public static synchronized void removeBeanDefinition(String beanName) {
    registry.removeBeanDefinition(beanName);
  }

  public static synchronized <T> T registerBeanDefinition(
      String beanName, Class<?> clazz, Object[] argValues, Map<String, Object> propertyValues) {
    BeanDefinitionBuilder beanDefinitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(clazz);
    beanDefinitionBuilder.setAutowireMode(AutoType.AUTOWIRE_BY_TYPE.getValue());

    for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
      beanDefinitionBuilder.addPropertyValue(entry.getKey(), entry.getValue());
    }

    for (Object argValue : argValues) {
      beanDefinitionBuilder.addConstructorArgValue(argValue);
    }

    registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());

    return getBean(beanName);
  }
}
