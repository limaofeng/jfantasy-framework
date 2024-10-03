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
package net.asany.jfantasy.autoconfigure;

import net.asany.jfantasy.graphql.client.GraphQLClientBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class GraphQLClientAutoConfiguration {

  @Bean
  static GraphQLClientBeanPostProcessor clientBeanPostProcessor(
      final ApplicationContext applicationContext, final ResourceLoader resourceLoader) {
    return new GraphQLClientBeanPostProcessor(applicationContext, resourceLoader);
  }
}
