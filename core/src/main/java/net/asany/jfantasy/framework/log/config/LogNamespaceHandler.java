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
package net.asany.jfantasy.framework.log.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author limaofeng
 */
public class LogNamespaceHandler extends NamespaceHandlerSupport {

  static final String LOG_MANAGER_ATTRIBUTE = "log-manager";
  static final String DEFAULT_LOG_MANAGER_BEAN_NAME = "logManager";

  static String extractCacheManager(Element element) {
    return element.hasAttribute(LogNamespaceHandler.LOG_MANAGER_ATTRIBUTE)
        ? element.getAttribute(LogNamespaceHandler.LOG_MANAGER_ATTRIBUTE)
        : LogNamespaceHandler.DEFAULT_LOG_MANAGER_BEAN_NAME;
  }

  static BeanDefinition parseKeyGenerator(Element element, BeanDefinition def) {
    String name = element.getAttribute("key-generator");
    if (StringUtils.hasText(name)) {
      def.getPropertyValues().add("keyGenerator", new RuntimeBeanReference(name.trim()));
    }
    return def;
  }

  @Override
  public void init() {
    registerBeanDefinitionParser(
        "annotation-driven", new AnnotationDrivenLogBeanDefinitionParser());
  }
}
