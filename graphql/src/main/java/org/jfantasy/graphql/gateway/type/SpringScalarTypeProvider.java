package org.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.graphql.gateway.config.GatewayConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class SpringScalarTypeProvider implements ScalarTypeProvider, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public String getName() {
    return "spring";
  }

  @Override
  @SneakyThrows
  public GraphQLScalarType getScalarType(GatewayConfig.ScalarConfig config) {
    if (StringUtil.isBlank(config.getResolver())) {
      return Arrays.stream(applicationContext.getBeanNamesForType(GraphQLScalarType.class))
          .filter(
              name -> {
                GraphQLScalarType scalarType =
                    applicationContext.getBean(name, GraphQLScalarType.class);
                return scalarType.getName().equals(config.getName());
              })
          .findFirst()
          .map(
              name -> {
                log.debug("获取到GraphQLScalarType:{}", name);
                return applicationContext.getBean(name, GraphQLScalarType.class);
              })
          .orElse(null);
    }
    return (GraphQLScalarType) applicationContext.getBean(config.getResolver());
  }

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }
}
