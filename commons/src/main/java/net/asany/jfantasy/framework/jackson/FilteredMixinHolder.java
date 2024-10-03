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
package net.asany.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.annotation.BeanFilter;
import net.asany.jfantasy.framework.jackson.annotation.JsonResultFilter;
import net.asany.jfantasy.framework.util.asm.AnnotationDescriptor;
import net.asany.jfantasy.framework.util.asm.AsmUtil;
import org.springframework.core.MethodParameter;

/**
 * MixInHolder
 *
 * @author limaofeng
 * @version V1.0
 * @date 07/11/2017 8:38 PM
 */
@Slf4j
public class FilteredMixinHolder {
  private static final SimpleFilterProvider defaultFilterProvider = new SimpleFilterProvider();
  private static final Map<Class<?>, MixInSource> mixInSourceMap = new ConcurrentHashMap<>();

  private static final String DEFAULT_PROVIDER_KEY = "default_provider_key";

  private static final ConcurrentMap<String, SimpleFilterProvider> PROVIDERS =
      new ConcurrentHashMap<>();

  static {
    PROVIDERS.put(DEFAULT_PROVIDER_KEY, new SimpleFilterProvider().setFailOnUnknownId(false));
  }

  public static MixInSource createMixInSource(Class<?> type) {
    if (!mixInSourceMap.containsKey(type)) {
      String uuid = UUID.randomUUID().toString().replaceAll("-", "");
      Class<?> mixIn =
          AsmUtil.makeInterface(
              "net.asany.jfantasy.framework.jackson.mixin." + type.getSimpleName() + "_" + uuid,
              AnnotationDescriptor.builder(JsonFilter.class).setValue("value", uuid).build());
      MixInSource mixInSource = new MixInSource(uuid, type, mixIn);
      mixInSourceMap.putIfAbsent(type, mixInSource);
      defaultFilterProvider.addFilter(
          mixInSource.getId(), FilteredMixinFilter.newBuilder(type).build());
      log.debug("createMixInSource: {}", mixInSource);
      return mixInSource;
    }
    return mixInSourceMap.get(type);
  }

  public static FilterProvider getDefaultFilterProvider() {
    return defaultFilterProvider;
  }

  public static SimpleFilterProvider getFilterProvider(JsonResultFilter jsonResultFilter) {
    String key = jsonResultFilter.toString();
    if (PROVIDERS.containsKey(key)) {
      return PROVIDERS.get(key);
    }
    SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
    FilteredMixinFilter propertyFilter = new FilteredMixinFilter();
    for (BeanFilter filter : jsonResultFilter.value()) {
      if (filter.type().isArray()) {
        continue;
      }
      createMixInSource(filter.type());
      propertyFilter
          .addFilter(filter.type())
          .includes(filter.includes())
          .excludes(filter.excludes());
    }
    provider.setDefaultFilter(propertyFilter);
    PROVIDERS.putIfAbsent(key, provider);
    return provider;
  }

  public static SimpleFilterProvider getFilterProvider(MethodParameter methodParameter) {
    JsonResultFilter jsonResultFilter =
        Objects.requireNonNull(methodParameter.getMethod()).getAnnotation(JsonResultFilter.class);
    if (jsonResultFilter == null) {
      return PROVIDERS.get(DEFAULT_PROVIDER_KEY);
    }
    return getFilterProvider(jsonResultFilter);
  }

  public static ClassIntrospector.MixInResolver getMixInResolver() {
    return new ClassIntrospector.MixInResolver() {
      @Override
      public Class<?> findMixInClassFor(Class<?> cls) {
        if (mixInSourceMap.containsKey(cls)) {
          return mixInSourceMap.get(cls).getMixIn();
        }
        return null;
      }

      @Override
      public ClassIntrospector.MixInResolver copy() {
        return this;
      }
    };
  }

  @Getter
  public static class MixInSource {
    private final String id;
    private final Class<?> type;
    private final Class<?> mixIn;

    MixInSource(String id, Class<?> type, Class<?> mixIn) {
      this.id = id;
      this.type = type;
      this.mixIn = mixIn;
    }
  }
}
