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
package net.asany.jfantasy.graphql.gateway.directive;

import graphql.language.DirectiveDefinition;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.config.Directive;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class DirectiveFactory implements ApplicationContextAware {

  private final Map<String, Directive> directiveMap = new HashMap<>();
  private ApplicationContext applicationContext;

  public ClientDirectiveHandler get(String directiveName) {
    if (!directiveMap.containsKey(directiveName)) {
      throw new IllegalArgumentException("Unknown directive: " + directiveName);
    }
    return directiveMap.get(directiveName).getHandler();
  }

  public Map<String, Directive> getDirectives() {
    return directiveMap;
  }

  public Map<String, DirectiveDefinition> getDirectiveDefinitions() {
    return directiveMap.values().stream()
        .collect(Collectors.toMap(Directive::getName, Directive::getDefinition));
  }

  public Directive registerDirective(DirectiveDefinition definition, String handlerClass) {
    if (directiveMap.containsKey(definition.getName())) {
      throw new IllegalArgumentException("Duplicate directive: " + definition.getName());
    }

    // 创建 DirectiveHandler
    ClientDirectiveHandler handler;
    try {
      Class<?> directiveClass = Class.forName(handlerClass);
      if (applicationContext != null) {
        handler = (ClientDirectiveHandler) applicationContext.getBean(directiveClass);
      } else {
        handler = (ClientDirectiveHandler) directiveClass.getConstructor().newInstance();
      }
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Unknown directive class: " + handlerClass);
    } catch (InvocationTargetException
        | NoSuchMethodException
        | IllegalAccessException
        | InstantiationException e) {
      throw new IllegalArgumentException("Failed to create directive class: " + handlerClass, e);
    }

    Directive directive =
        Directive.builder()
            .name(definition.getName())
            .definition(definition)
            .handler(handler)
            .build();

    directiveMap.put(definition.getName(), directive);
    return directive;
  }

  public Directive registerDirective(String definitionSource, String handlerClass) {
    Directive directive =
        registerDirective(
            GraphQLTypeUtils.parseDirectiveDefinition(definitionSource), handlerClass);
    directive.setDefinitionSource(definitionSource);
    return directive;
  }

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  public void registerDefaultDirectives() {
    log.info("Register date format directive");
    this.registerDirective(DateFormatDirective.DEFINITION, DateFormatDirective.class.getName());
    log.info("Register number format directive");
    this.registerDirective(NumberFormatDirective.DEFINITION, NumberFormatDirective.class.getName());
  }
}
