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
package net.asany.jfantasy.framework.dao.hibernate.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import net.asany.jfantasy.framework.dao.hibernate.generator.SnowflakeIdentifierGenerator;
import net.asany.jfantasy.framework.util.common.PaddingType;
import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(SnowflakeIdentifierGenerator.class)
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface SnowflakeGenerator {
  /**
   * 工作机器ID
   *
   * @return 工作机器ID
   */
  long workerId() default 1;

  /**
   * 数据中心ID
   *
   * @return 数据中心ID
   */
  long dataCenterId() default 1;

  /**
   * 格式化
   *
   * @return 格式化
   */
  SnowflakeFormat format() default SnowflakeFormat.NONE;

  /**
   * 长度 默认0 只在 format 为 BASE62 时有效
   *
   * @return 长度
   */
  long length() default 0;

  /**
   * 补位方式
   *
   * @return 补位方式
   */
  PaddingType paddingType() default PaddingType.SUFFIX;
}
