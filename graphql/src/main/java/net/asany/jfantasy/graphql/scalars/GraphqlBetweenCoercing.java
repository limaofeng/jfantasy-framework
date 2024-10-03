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

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import net.asany.jfantasy.framework.util.common.DateUtil;
import net.asany.jfantasy.graphql.DateBetween;
import net.asany.jfantasy.graphql.util.Kit;
import org.jetbrains.annotations.NotNull;

public class GraphqlBetweenCoercing implements Coercing<DateBetween, String> {
  @Override
  public String serialize(Object input) throws CoercingSerializeException {
    return input.toString();
  }

  @Override
  public DateBetween parseValue(Object input) throws CoercingParseValueException {
    if (!input.toString().contains(",")) {
      return null;
    }
    String[] inputs = input.toString().split(",");
    return DateBetween.newDateBetween(
        DateUtil.parseFormat(inputs[0]), DateUtil.parseFormat(inputs[1]));
  }

  @Override
  public DateBetween parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
    if (!(input instanceof StringValue)) {
      throw new CoercingParseLiteralException(
          "Expected AST type 'StringValue' but was '" + Kit.typeName(input) + "'.");
    }
    return this.parseValue(((StringValue) input).getValue());
  }
}
