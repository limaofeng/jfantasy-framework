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

import net.asany.jfantasy.framework.spring.config.WebMvcConfig;
import net.asany.jfantasy.framework.web.tomcat.CustomTomcatContextCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 自定义 WebStarter AutoConfiguration
 *
 * @author limaofeng
 */
@ConditionalOnClass(EnableWebMvc.class)
@Configuration
@Import(WebMvcConfig.class)
@ComponentScan({"net.asany.jfantasy.framework.spring.mvc.servlet"})
public class WebStarterAutoConfiguration {

  /**
   * TODO: 禁用自带的 Tomcat Session, * 直接禁用，会导致 GraphQL 的 WebSocket 无法使用
   *
   * @return CustomTomcatContextCustomizer
   */
  public CustomTomcatContextCustomizer customTomcatContextCustomizer() {
    return new CustomTomcatContextCustomizer();
  }
}
