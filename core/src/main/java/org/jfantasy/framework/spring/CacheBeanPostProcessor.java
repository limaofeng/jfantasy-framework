package org.jfantasy.framework.spring;

import java.util.Arrays;
import java.util.List;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.interceptor.CacheInterceptor;

/**
 * 提前初始化 CacheInterceptor
 *
 * @author limaofeng
 */
public class CacheBeanPostProcessor implements BeanPostProcessor {

  private static final String CACHE_INTERCEPTOR_BEAN_NAME = "cacheInterceptor";

  private final List<String> beanNames;

  public CacheBeanPostProcessor(String... beanNames) {
    this.beanNames = Arrays.asList(beanNames);
  }

  @Override
  public Object postProcessBeforeInitialization(
      @SuppressWarnings("NullableProblems") Object bean,
      @SuppressWarnings("NullableProblems") String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(
      @SuppressWarnings("NullableProblems") Object bean,
      @SuppressWarnings("NullableProblems") String beanName)
      throws BeansException {
    if (beanNames.contains(beanName) && SpringBeanUtils.containsBean(CACHE_INTERCEPTOR_BEAN_NAME)) {
      CacheInterceptor cacheInterceptor = SpringBeanUtils.getBean(CACHE_INTERCEPTOR_BEAN_NAME);
      if (cacheInterceptor != null) {
        boolean initialized = ClassUtil.getValue(cacheInterceptor, "initialized");
        if (!initialized) {
          cacheInterceptor.afterSingletonsInstantiated();
        }
      }
    }
    return bean;
  }
}
