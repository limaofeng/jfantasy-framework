package org.jfantasy.graphql.scalars;

import static org.jfantasy.graphql.util.Kit.typeName;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.data.domain.Sort;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019/10/8 1:53 下午
 */
@Slf4j
public class OrderCoercing implements Coercing<Sort, String> {

  @Override
  public String serialize(Object input) throws CoercingSerializeException {
    return input.toString();
  }

  @Override
  public Sort parseValue(Object input) throws CoercingParseValueException {
    String inputString = input.toString();
    if ("unsorted".equals(inputString)) {
      return Sort.unsorted();
    }
    if (inputString.contains(",")) {
      return Sort.by(
          Arrays.stream(inputString.split(","))
              .filter(StringUtil::isNotBlank)
              .map(this::parseValue)
              .reduce(
                  new ArrayList<Sort.Order>(),
                  (all, item) -> {
                    all.addAll(item.toList());
                    return all;
                  },
                  (acc, bcc) -> {
                    acc.addAll(bcc);
                    return acc;
                  })
              .toArray(new Sort.Order[0]));
    }
    if (inputString.contains("(")) {
      // 语法为: createdAt_desc(NATIVE)
      String[] split = inputString.split("\\(");
      String[] sort = split[0].split("_");
      return Sort.by(
          new Sort.Order(
              Sort.Direction.valueOf(sort[1].toUpperCase()),
              sort[0],
              Sort.NullHandling.valueOf(
                  split[1].substring(0, split[1].length() - 1).toUpperCase())));
    } else {
      String[] sort = inputString.split("_");
      return Sort.by(Sort.Direction.valueOf(sort[1].toUpperCase()), sort[0]);
    }
  }

  @Override
  public Sort parseLiteral(Object input) throws CoercingParseLiteralException {
    if (!(input instanceof StringValue)) {
      throw new CoercingParseLiteralException(
          "Expected AST type 'StringValue' but was '" + typeName(input) + "'.");
    }
    return this.parseValue(((StringValue) input).getValue());
  }
}
