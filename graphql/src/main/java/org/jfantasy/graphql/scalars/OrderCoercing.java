package org.jfantasy.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.jfantasy.framework.dao.OrderBy;
import org.jfantasy.framework.util.common.StringUtil;

import java.util.Arrays;

import static org.jfantasy.graphql.util.Kit.typeName;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019/10/8 1:53 下午
 */
public class OrderCoercing implements Coercing<OrderBy, String> {

    @Override
    public String serialize(Object input) throws CoercingSerializeException {
        return input.toString();
    }

    @Override
    public OrderBy parseValue(Object input) throws CoercingParseValueException {
        if (input.toString().contains(",")) {
            return OrderBy.by(Arrays.stream(input.toString().split(",")).filter(item -> StringUtil.isNotBlank(item)).map(item -> this.parseValue(item)).toArray(size -> new OrderBy[size]));
        }
        String[] sort = input.toString().split("_");
        return OrderBy.newOrderBy(sort[0], OrderBy.Direction.valueOf(sort[1].toUpperCase()));
    }

    @Override
    public OrderBy parseLiteral(Object input) throws CoercingParseLiteralException {
        if (!(input instanceof StringValue)) {
            throw new CoercingParseLiteralException(
                "Expected AST type 'StringValue' but was '" + typeName(input) + "'."
            );
        }
        return this.parseValue(((StringValue) input).getValue());
    }

}
