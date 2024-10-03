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
import net.asany.jfantasy.framework.dao.hibernate.generator.TableIdentifierGenerator;
import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(TableIdentifierGenerator.class)
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface TableGenerator {
  String name() default "";

  int initialValue() default 0;

  int allocationSize() default 50;

  int incrementSize() default 1;
}
