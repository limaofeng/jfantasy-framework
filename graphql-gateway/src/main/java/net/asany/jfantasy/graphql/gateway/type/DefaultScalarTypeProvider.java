package net.asany.jfantasy.graphql.gateway.type;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.*;
import graphql.schema.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;
import org.jetbrains.annotations.NotNull;

public class DefaultScalarTypeProvider implements ScalarTypeProvider {
  @Override
  public String getName() {
    return "default";
  }

  @Override
  public GraphQLScalarType getScalarType(GatewayConfig.ScalarConfig config) {
    return GraphQLScalarType.newScalar()
        .name(config.getName())
        .description(config.getDescription())
        .coercing(new DefaultCoercing(config))
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
      return Coercing.super.parseValue(input, graphQLContext, locale);
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
