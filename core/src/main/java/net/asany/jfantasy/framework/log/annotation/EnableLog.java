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

import static org.springframework.context.annotation.AdviceMode.PROXY;

import java.lang.annotation.*;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * 启用日志
 *
 * @author limaofeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogConfigurationSelector.class)
public @interface EnableLog {

  /**
   * 代理目标类
   *
   * @return bool
   */
  boolean proxyTargetClass() default false;

  /**
   * 建议模式
   *
   * @return AdviceMode
   */
  AdviceMode mode() default PROXY;

  /**
   * 排序
   *
   * @return int
   */
  int order() default Ordered.LOWEST_PRECEDENCE;
}
