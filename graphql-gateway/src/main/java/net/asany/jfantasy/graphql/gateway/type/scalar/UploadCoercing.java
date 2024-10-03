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
package net.asany.jfantasy.graphql.gateway.type.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.NullValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public class UploadCoercing implements Coercing<Object, Object> {

  @Override
  public Object serialize(
      @NotNull Object dataFetcherResult,
      @NotNull GraphQLContext graphQLContext,
      @NotNull Locale locale)
      throws CoercingSerializeException {
    return dataFetcherResult;
  }

  @Override
  public Object parseValue(
      @NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale)
      throws CoercingParseValueException {
    return input;
  }

  @Override
  public @NotNull Object parseLiteral(
      @NotNull Value<?> input,
      @NotNull CoercedVariables variables,
      @NotNull GraphQLContext graphQLContext,
      @NotNull Locale locale)
      throws CoercingParseLiteralException {
    return NullValue.newNullValue();
  }

  @Override
  public @NotNull Value<?> valueToLiteral(
      @NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) {
    return Coercing.super.valueToLiteral(input, graphQLContext, locale);
  }
}
