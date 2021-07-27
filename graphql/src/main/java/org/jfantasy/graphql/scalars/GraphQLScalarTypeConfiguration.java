package org.jfantasy.graphql.scalars;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.graphql.DateBetween;
import org.jfantasy.graphql.util.Kit;
import org.springframework.context.annotation.Bean;

import java.util.Date;

import static org.jfantasy.graphql.util.Kit.typeName;

/**
 * GraphQL ScalarType Configuration
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019/8/23 6:06 下午
 */
@Slf4j
public class GraphQLScalarTypeConfiguration {

    @Bean
    public GraphQLScalarType jsonScalar() {
        return ExtendedScalars.Json;
    }

    @Bean
    public GraphQLScalarType orderByScalar() {
        return GraphQLScalarType.newScalar().name("OrderBy").description("排序对象, 格式如：createdAt_ASC ").coercing(new OrderCoercing()).build();
    }

    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar().name("Date").description(" Date 转换类").coercing(new Coercing<Date, Object>() {
            @Override
            public Object serialize(Object input) throws CoercingSerializeException {
                if (input instanceof Date) {
                    return ((Date) input).getTime();
                }
                return input;
            }

            @Override
            public Date parseValue(Object input) throws CoercingParseValueException {
                if (input instanceof Date) {
                    return (Date) input;
                }
                if (!(input instanceof String)) {
                    throw new CoercingParseValueException("Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '" + Kit.typeName(input) + "'.");
                }
                return ReflectionUtils.convert(input, Date.class);
            }

            @Override
            public Date parseLiteral(Object input) throws CoercingParseLiteralException {
                if (input instanceof StringValue) {
                    return ReflectionUtils.convert(((StringValue) input).getValue(), Date.class);
                }
                if (input instanceof IntValue) {
                    return new Date(((IntValue) input).getValue().longValue());
                }
                return null;
            }
        }).build();
    }


    @Bean
    public GraphQLScalarType dateBetweenScalar() {
        return GraphQLScalarType.newScalar().name("DateBetween").description("时间区间参数").coercing(new Coercing<DateBetween, String>() {
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
                return DateBetween.newDateBetween(DateUtil.parseFormat(inputs[0]), DateUtil.parseFormat(inputs[1]));
            }

            @Override
            public DateBetween parseLiteral(Object input) throws CoercingParseLiteralException {
                if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException(
                        "Expected AST type 'StringValue' but was '" + typeName(input) + "'."
                    );
                }
                return this.parseValue(((StringValue) input).getValue());
            }
        }).build();
    }
}
