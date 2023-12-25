package net.asany.jfantasy.graphql.scalars;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.util.Date;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.graphql.util.Kit;
import org.jetbrains.annotations.NotNull;

public class GraphqlDateCoercing implements Coercing<Date, Object> {

  @Override
  public Object serialize(@NotNull Object input) throws CoercingSerializeException {
    if (input instanceof Date) {
      return ((Date) input).getTime();
    }
    return input;
  }

  @Override
  public Date parseValue(@NotNull Object input) throws CoercingParseValueException {
    if (input instanceof Date) {
      return (Date) input;
    }
    if (input instanceof Long) {
      return new Date((Long) input);
    }
    if (!(input instanceof String)) {
      throw new CoercingParseValueException(
          "Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '"
              + Kit.typeName(input)
              + "'.");
    }
    return ReflectionUtils.convert(input, Date.class);
  }

  @Override
  public Date parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
    if (input instanceof StringValue) {
      return ReflectionUtils.convert(((StringValue) input).getValue(), Date.class);
    }
    if (input instanceof IntValue) {
      return new Date(((IntValue) input).getValue().longValue());
    }
    return null;
  }
}
