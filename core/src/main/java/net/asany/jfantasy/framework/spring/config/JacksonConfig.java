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

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Method;
import java.util.*;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.*;
import net.asany.jfantasy.framework.jackson.annotation.JsonResultFilter;
import net.asany.jfantasy.framework.jackson.deserializer.DateDeserializer;
import net.asany.jfantasy.framework.jackson.serializer.DateSerializer;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;

@Slf4j
@Order(value = JacksonConfig.ORDER)
@Configuration
public class JacksonConfig {

  public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 30;

  @Bean("JsonObjectMapperWrapper")
  public ObjectMapperWrapper json(ObjectMapper objectMapper) {
    return JSON.initialize(objectMapper);
  }

  @Bean("xmlObjectMapperWrapper")
  public ObjectMapperWrapper xml(
      List<Jackson2ObjectMapperBuilderCustomizer> objectMapperBuilderCustomizers,
      List<Jackson2XmlMapperBuilderCustomizer> xmlMapperBuilderCustomizers) {
    return XML.initialize(
        xmlMapperBuilder -> {
          for (Jackson2ObjectMapperBuilderCustomizer customizer : objectMapperBuilderCustomizers) {
            customizer.customize(xmlMapperBuilder);
          }
          for (Jackson2XmlMapperBuilderCustomizer customizer : xmlMapperBuilderCustomizers) {
            customizer.customize(xmlMapperBuilder);
          }
          return xmlMapperBuilder;
        });
  }

  @Bean
  public ObjectMapperBeanPostProcessor objectMapperBeanPostProcessor() {
    return new ObjectMapperBeanPostProcessor();
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer defaultCustomizeJackson(
      ApplicationContext context, List<Module> modules) {
    return new AnyJackson2ObjectMapperBuilderCustomizer(context, modules);
  }

  @Bean
  public Jackson2XmlMapperBuilderCustomizer defaultCustomizeJacksonXml() {
    return new AnyJackson2XmlMapperBuilderCustomizer();
  }

  public static class AnyJackson2ObjectMapperBuilderCustomizer
      implements Jackson2ObjectMapperBuilderCustomizer {

    private final List<Module> modules;
    private final ApplicationContext context;

    public AnyJackson2ObjectMapperBuilderCustomizer(
        ApplicationContext context, List<Module> modules) {
      this.modules = modules == null ? new ArrayList<>() : modules;
      this.context = context;
    }

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {

      Map<Class<?>, Class<?>> mixIns = new HashMap<>();

      if (context != null) {
        preloadMixIns(context, mixIns);
      }

      this.modules.add(new JavaTimeModule());
      this.modules.add(
          new SimpleModule()
              .addSerializer(Date.class, new DateSerializer("yyyy-MM-dd HH:mm:ss"))
              .addDeserializer(Date.class, new DateDeserializer()));

      builder
          .modules(modules)
          .filters(FilteredMixinHolder.getDefaultFilterProvider())
          .mixIns(mixIns);
    }

    private void preloadMixIns(ApplicationContext context, Map<Class<?>, Class<?>> mixIns) {
      StopWatch watch = new StopWatch();
      watch.start();
      // 获取所有组件扫描的包路径
      String[] componentScanPackages =
          context.getBeansWithAnnotation(ComponentScan.class).values().stream()
              .map(
                  bean -> {
                    ComponentScan componentScan =
                        ClassUtil.getRealClass(bean.getClass()).getAnnotation(ComponentScan.class);
                    return ObjectUtil.join(componentScan.value(), componentScan.basePackages());
                  })
              .flatMap(Arrays::stream)
              .toArray(String[]::new);

      ClassPathScanningCandidateComponentProvider scanner =
          new ClassPathScanningCandidateComponentProvider(false);
      scanner.setEnvironment(context.getEnvironment());
      scanner.setResourceLoader(context);
      scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

      // 扫描包中的类和方法，并查找带有 JsonResultFilter 注解的方法
      for (String basePackage : componentScanPackages) {
        Set<Method> methods = scanForJsonResultFilterMethods(scanner, basePackage);

        for (Method method : methods) {
          MethodParameter methodParameter = new MethodParameter(method, -1);

          SimpleFilterProvider filterProvider =
              FilteredMixinHolder.getFilterProvider(methodParameter);
          PropertyFilter propertyFilter = filterProvider.getDefaultFilter();
          if (propertyFilter instanceof FilteredMixinFilter filteredMixinFilter) {
            for (Class<?> type : filteredMixinFilter.getTypes()) {
              if (mixIns.containsKey(type)) {
                continue;
              }
              FilteredMixinHolder.MixInSource mixInSource =
                  FilteredMixinHolder.createMixInSource(type);
              mixIns.put(type, mixInSource.getMixIn());
            }
          }
        }
      }
      log.info("预加载 Controller 中的 JsonResultFilter 注解完成,耗时 {} ms", watch.getTotalTimeMillis());
    }

    private Set<Method> scanForJsonResultFilterMethods(
        ClassPathScanningCandidateComponentProvider scanner, String basePackage) {
      Set<Method> methods = new HashSet<>();
      for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
        try {
          Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
          for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(JsonResultFilter.class)) {
              methods.add(method);
            }
          }
        } catch (ClassNotFoundException e) {
          // 处理异常
          log.error(e.getMessage());
        }
      }
      return methods;
    }
  }

  public static class AnyJackson2XmlMapperBuilderCustomizer
      implements Jackson2XmlMapperBuilderCustomizer {
    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
      JacksonXmlModule xmlModule = new JacksonXmlModule();
      builder
          .modulesToInstall(xmlModule)
          .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    }
  }

  public static class ObjectMapperBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
        throws BeansException {
      if (bean instanceof ObjectMapper objectMapper) {
        Unirest.config().setObjectMapper(new UnirestObjectMapper(objectMapper));
      }
      return bean;
    }
  }
}
