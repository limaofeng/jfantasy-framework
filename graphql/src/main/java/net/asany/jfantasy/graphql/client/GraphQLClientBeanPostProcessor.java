package net.asany.jfantasy.graphql.client;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * GraphQL 客户端 BeanPostProcessor
 *
 * @author limaofeng
 */
public class GraphQLClientBeanPostProcessor implements BeanPostProcessor {

  private final ResourceLoader resourceLoader;
  private final ApplicationContext applicationContext;

  private final Map<String, GraphQLTemplate> clientMap = new HashMap<>();

  public GraphQLClientBeanPostProcessor(
      final ApplicationContext applicationContext, ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
    this.applicationContext = requireNonNull(applicationContext, "applicationContext");
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, @NotNull final String beanName)
      throws BeansException {
    Class<?> clazz = bean.getClass();
    do {
      for (final Field field : clazz.getDeclaredFields()) {
        final GraphQLClient annotation = AnnotationUtils.findAnnotation(field, GraphQLClient.class);
        if (annotation != null) {
          ReflectionUtils.makeAccessible(field);
          ReflectionUtils.setField(
              field, bean, processInjectionPoint(field, field.getType(), annotation));
        }
      }
      clazz = clazz.getSuperclass();
    } while (clazz != null);
    return bean;
  }

  protected <T> T processInjectionPoint(
      final Member injectionTarget, final Class<T> injectionType, final GraphQLClient annotation) {
    final String name = annotation.value();
    final T value = valueForMember(name, injectionTarget, injectionType);
    if (value == null) {
      throw new IllegalStateException(
          "Injection value is null unexpectedly for " + name + " at " + injectionTarget);
    }
    return value;
  }

  protected <T> T valueForMember(
      final String name, final Member injectionTarget, final Class<T> injectionType)
      throws BeansException {
    if (clientMap.containsKey(name)) {
      //noinspection unchecked
      return (T) clientMap.get(name);
    }
    Environment environment = this.applicationContext.getEnvironment();
    String url = environment.getProperty("graphql.client." + name + ".address");
    if (StringUtil.isBlank(url) || !GraphQLTemplate.class.isAssignableFrom(injectionType)) {
      throw new BeanInstantiationException(
          injectionType, "Failed to create GraphQL Client of Name " + name);
    }
    GraphQLTemplate graphQLTemplate = createGraphQLTemplate(url);
    clientMap.put(name, graphQLTemplate);
    //noinspection unchecked
    return (T) graphQLTemplate;
  }

  public GraphQLTemplate createGraphQLTemplate(String url) {
    RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
    ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
    return new GraphQLTemplate(resourceLoader, restTemplate, url, objectMapper);
  }
}
