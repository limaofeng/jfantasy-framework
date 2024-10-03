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
package net.asany.jfantasy.framework.search.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 同一个Entity类的不同的Document，可能需要设置不同的权重<br>
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-23 下午03:15:53
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BoostSwitch {
  /**
   * 与@IndexFilter的compare含义相同
   *
   * @return Compare
   */
  Compare compare();

  /**
   * 与@IndexFilter的value含义相同
   *
   * @return String
   */
  String value();

  /**
   * float型，表示满足比较条件时该Document的boost值，缺省值为1.0
   *
   * @return float
   */
  float fit() default 1.0f;

  /**
   * 表示不满足比较条件时该Document的boost值，缺省值为1.0
   *
   * @return float
   */
  float unfit() default 1.0f;
}
