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
package net.asany.jfantasy.graphql.gateway.type;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.*;
import graphql.schema.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;
import net.asany.jfantasy.graphql.gateway.type.scalar.UploadCoercing;
import org.jetbrains.annotations.NotNull;

public class DefaultScalarTypeProvider implements ScalarTypeProvider {
  @Override
  public String getName() {
    return "default";
  }

  @Override
  public GraphQLScalarType getScalarType(GatewayConfig.ScalarConfig config) {
    Coercing<?, ?> coercing;
    if ("Upload".equals(StringUtil.defaultValue(config.getProvider(), config::getName))) {
      coercing = new UploadCoercing();
    } else {
      coercing = new DefaultCoercing(config);
    }
    return GraphQLScalarType.newScalar()
        .name(config.getName())
        .description(config.getDescription())
        .coercing(coercing)
        .build();
  }

  private static class DefaultCoercing implements Coercing<Object, Object> {
    private final GatewayConfig.ScalarConfig scalarConfig;

    public DefaultCoercing(GatewayConfig.ScalarConfig config) {
      this.scalarConfig = config;
    }

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
      if (input instanceof StringValue value) {
        return value.getValue();
      } else if (input instanceof IntValue value) {
        return value.getValue();
      } else if (input instanceof FloatValue value) {
        return value.getValue();
      } else if (input instanceof BooleanValue value) {
        return value.isValue();
      } else if (input instanceof EnumValue value) {
        return value.getName();
      }
      if (input instanceof ArrayValue value) {
        return value.getValues().stream()
            .map(v -> parseLiteral(v, variables, graphQLContext, locale))
            .toArray();
      } else if (input instanceof ObjectValue value) {
        Map<String, Object> map = new HashMap<>();
        for (ObjectField v : value.getObjectFields()) {
          map.put(v.getName(), parseLiteral(v.getValue(), variables, graphQLContext, locale));
        }
        return map;
      }
      return NullValue.newNullValue();
    }

    @Override
    public @NotNull Value<?> valueToLiteral(
        @NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) {
      return Coercing.super.valueToLiteral(input, graphQLContext, locale);
    }
  }
}
