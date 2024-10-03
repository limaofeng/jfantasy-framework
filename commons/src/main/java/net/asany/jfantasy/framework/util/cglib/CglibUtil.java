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
package net.asany.jfantasy.framework.util.cglib;

import java.util.concurrent.ConcurrentHashMap;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class CglibUtil {
  private CglibUtil() {}

  private static final ConcurrentHashMap<String, MethodInterceptor> defaultInterceptors =
      new ConcurrentHashMap<>();

  private static final ConcurrentHashMap<Class<?>, Enhancer> enhancerCache =
      new ConcurrentHashMap<>();

  public static <T> T newInstance(Class<T> classType, MethodInterceptor interceptor) {
    if (!enhancerCache.containsKey(classType)) {
      enhancerCache.putIfAbsent(classType, newEnhancer(classType, interceptor));
    }
    return (T) enhancerCache.get(classType).create();
  }

  private static <T> Enhancer newEnhancer(Class<T> classType, MethodInterceptor interceptor) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(classType);
    enhancer.setCallback(interceptor);
    return enhancer;
  }

  public static MethodInterceptor getDefaultInterceptor(String key) {
    return defaultInterceptors.get(key);
  }
}
