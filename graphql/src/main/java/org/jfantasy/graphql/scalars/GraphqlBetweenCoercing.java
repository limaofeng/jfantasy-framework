package org.jfantasy.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.graphql.DateBetween;
import org.jfantasy.graphql.util.Kit;

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
  public DateBetween parseLiteral(Object input) throws CoercingParseLiteralException {
    if (!(input instanceof StringValue)) {
      throw new CoercingParseLiteralException(
          "Expected AST type 'StringValue' but was '" + Kit.typeName(input) + "'.");
    }
    return this.parseValue(((StringValue) input).getValue());
  }
}
