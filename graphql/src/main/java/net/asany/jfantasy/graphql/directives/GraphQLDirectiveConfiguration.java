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
package net.asany.jfantasy.graphql.directives;

import graphql.kickstart.autoconfigure.tools.SchemaDirective;
import org.springframework.context.annotation.Bean;

/**
 * GraphQL 指令配置
 *
 * @author limaofeng
 */
public class GraphQLDirectiveConfiguration {

  @Bean
  public SchemaDirective dateFormattingDirective() {
    return new SchemaDirective("dateFormat", new DateFormatDirective());
  }

  @Bean
  public SchemaDirective fileSizeDirective() {
    return new SchemaDirective("fileSize", new FileSizeDirective());
  }

  @Bean
  public SchemaDirective numberFormatDirective() {
    return new SchemaDirective("numberFormat", new NumberFormatDirective());
  }
}
