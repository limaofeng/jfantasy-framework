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

import java.lang.annotation.*;
import net.asany.jfantasy.framework.log.filter.DefaultLogFilter;

/**
 * 日志
 *
 * @author limaofeng
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log {
  /**
   * 简要描述信息
   *
   * @return String
   */
  String text() default "";

  /**
   * 判断是否记录日志
   *
   * @return String
   */
  String condition() default "";

  /**
   * 日志连接器类型
   *
   * @return String
   */
  String type() default "simple";

  /**
   * 日志拦截器
   *
   * @return Class
   */
  Class<?> using() default DefaultLogFilter.class;
}
