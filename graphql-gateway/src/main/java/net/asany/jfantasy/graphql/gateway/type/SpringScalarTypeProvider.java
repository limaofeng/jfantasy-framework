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
package net.asany.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;
import org.jetbrains.annotations.NotNull;
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
