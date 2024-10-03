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
package net.asany.jfantasy.graphql.scalars;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import net.asany.jfantasy.graphql.util.Kit;
import org.jetbrains.annotations.NotNull;

public class GraphqlNumberCoercing implements Coercing<Number, Object> {

  @Override
  public Object serialize(@NotNull Object input) throws CoercingSerializeException {
    return input;
  }

  @Override
  public Number parseValue(@NotNull Object input) throws CoercingParseValueException {
    throw new CoercingSerializeException("只能用于返回结果'" + Kit.typeName(input) + "'.");
  }

  @Override
  public Number parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
    throw new CoercingSerializeException("只能用于返回结果'" + Kit.typeName(input) + "'.");
  }
}
