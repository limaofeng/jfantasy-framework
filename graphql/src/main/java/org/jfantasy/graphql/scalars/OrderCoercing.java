package org.jfantasy.graphql.scalars;

import static org.jfantasy.graphql.util.Kit.typeName;

import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.OrderBy;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.data.domain.Sort;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019/10/8 1:53 下午
 */
@Slf4j
public class OrderCoercing implements Coercing<OrderBy, String> {

  @Override
  public String serialize(Object input) throws CoercingSerializeException {
    return input.toString();
  }

  @Override
  public OrderBy parseValue(Object input) throws CoercingParseValueException {
    String inputString = input.toString();
    if (inputString.contains(",")) {
      return OrderBy.by(
          Arrays.stream(inputString.split(","))
              .filter(StringUtil::isNotBlank)
              .map(this::parseValue)
              .toArray(OrderBy[]::new));
    }
    if (inputString.contains("(")) {
      String[] split = inputString.split("\\(");
      String[] sort = split[0].split("_");
      return OrderBy.newOrderBy(
          sort[0],
          OrderBy.Direction.valueOf(sort[1].toUpperCase()),
          Sort.NullHandling.valueOf(split[1].substring(0, split[1].length() - 1).toUpperCase()));
    } else {
      String[] sort = inputString.split("_");
      return OrderBy.newOrderBy(sort[0], OrderBy.Direction.valueOf(sort[1].toUpperCase()));
    }
  }

  @Override
  public OrderBy parseLiteral(Object input) throws CoercingParseLiteralException {
    if (input instanceof ObjectValue) {
      List<ObjectField> fields = ((ObjectValue) input).getObjectFields();
      if (!fields.isEmpty()) {
        log.warn("OrderBy 类型的对象赋值,只用来设置空排序. 设置无效!");
      }
      return OrderBy.unsorted();
    }
    if (!(input instanceof StringValue)) {
      throw new CoercingParseLiteralException(
          "Expected AST type 'StringValue' but was '" + typeName(input) + "'.");
    }
    return this.parseValue(((StringValue) input).getValue());
  }
}
