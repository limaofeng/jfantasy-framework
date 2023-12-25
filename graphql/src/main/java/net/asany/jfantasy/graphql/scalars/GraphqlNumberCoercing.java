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
