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

import java.util.Arrays;
import java.util.List;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.jetbrains.annotations.NotNull;
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
  public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
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
