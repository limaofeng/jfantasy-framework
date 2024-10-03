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
package net.asany.jfantasy.framework.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface BeanFilter {
  /**
   * 要忽略字段的POJO <br>
   * 2013-9-27 下午4:27:08
   *
   * @return type
   */
  Class<?> type();

  /**
   * 要包含的字段名 <br>
   * 2013-9-27 下午4:27:12
   *
   * @return names
   */
  String[] includes() default {};

  /**
   * 要忽略的字段名 <br>
   *
   * @return names
   */
  String[] excludes() default {};
}
