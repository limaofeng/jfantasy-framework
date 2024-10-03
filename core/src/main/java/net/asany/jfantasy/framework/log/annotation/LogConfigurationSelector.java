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
package net.asany.jfantasy.framework.log.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * 日志配置选择器
 *
 * @author limaofeng
 */
public class LogConfigurationSelector extends AdviceModeImportSelector<EnableLog> {

  private static final String LOG_ASPECT_CONFIGURATION_CLASS_NAME =
      "org.springframework.log.aspectj.AspectJCachingConfiguration";

  @Override
  public String[] selectImports(AdviceMode adviceMode) {
    return switch (adviceMode) {
      case PROXY -> new String[] {
        AutoProxyRegistrar.class.getName(), ProxyLogConfiguration.class.getName()
      };
      case ASPECTJ -> new String[] {LOG_ASPECT_CONFIGURATION_CLASS_NAME};
    };
  }
}
