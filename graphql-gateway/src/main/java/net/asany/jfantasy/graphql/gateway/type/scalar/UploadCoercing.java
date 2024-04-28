package net.asany.jfantasy.graphql.gateway.type.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.NullValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

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
