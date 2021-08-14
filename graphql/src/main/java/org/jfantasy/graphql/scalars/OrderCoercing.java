package org.jfantasy.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.jfantasy.framework.dao.OrderBy;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

import static org.jfantasy.graphql.util.Kit.typeName;

/**
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2019/10/8 1:53 下午
 */
public class OrderCoercing implements Coercing<OrderBy, String> {

    @Override
    public String serialize(Object input) throws CoercingSerializeException {
        return input.toString();
    }

    @Override
    public OrderBy parseValue(Object input) throws CoercingParseValueException {
        String inputString = input.toString();
        if (inputString.contains(",")) {
            return OrderBy.by(Arrays.stream(inputString.split(",")).filter(item -> StringUtil.isNotBlank(item)).map(item -> this.parseValue(item)).toArray(size -> new OrderBy[size]));
        }
        if (inputString.contains("(")){
            String[] split = inputString.split("\\(");
            String[] sort = split[0].split("_");
            return OrderBy.newOrderBy(sort[0], OrderBy.Direction.valueOf(sort[1].toUpperCase()), Sort.NullHandling.valueOf(split[1].substring(0, split[1].length() - 1).toUpperCase()));
        }else {
            String[] sort = inputString.split("_");
            return OrderBy.newOrderBy(sort[0], OrderBy.Direction.valueOf(sort[1].toUpperCase()));
        }
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
