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
 * 表示需要嵌入到其它对象的@Ref或@RefList域的索引中。
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-23 下午03:07:36
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexRefBy {
  /**
   * value——Class类型，表示被引用的类
   *
   * @return Class[]
   */
  Class<?>[] value();

  /**
   * 是否为字段建立索引
   *
   * @return boolean[]
   */
  boolean[] index() default false;

  /**
   * store——boolean型，表示是否需要存储
   *
   * @return boolean[]
   */
  boolean[] store() default true;

  /**
   * boost——float型，表示该Field的权重
   *
   * @return double[]
   */
  double[] boost() default 1.0f;
}
