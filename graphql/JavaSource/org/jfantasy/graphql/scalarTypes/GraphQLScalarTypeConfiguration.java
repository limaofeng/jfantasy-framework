package org.jfantasy.graphql.scalarTypes;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import graphql.language.StringValue;
import graphql.scalars.object.JsonScalar;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.OrderBy;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.storage.FileObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Date;

import static org.jfantasy.graphql.util.Kit.typeName;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019/8/23 6:06 下午
 */
@Slf4j
public class GraphQLScalarTypeConfiguration {

    @Value("${storage.url}")
    private String storageUrl;

    @Bean
    public GraphQLScalarType jsonScalar() {
        return new JsonScalar();
    }

    @Bean
    public GraphQLScalarType fileByScalar() {
        return GraphQLScalarType.newScalar().name("FileObject").description("文件对象").coercing(new Coercing<FileObject, Object>() {
            @Override
            public Object serialize(Object input) throws CoercingSerializeException {
                if (input instanceof FileObject) {
                    return input;
                }
                return input;
            }

            @Override
            public FileObject parseValue(Object input) throws CoercingParseValueException {
                String fileId = null;
                if (input instanceof String) {
                    fileId = input.toString();
                }
                if (input instanceof StringValue) {
                    fileId = ((StringValue) input).getValue();
                }
                if (fileId == null) {
                    return null;
                }
                try {
                    HttpResponse<FileObject> response = Unirest.get(storageUrl + "/files/" + fileId).asObject(FileObject.class);
                    return response.getBody();
                } catch (UnirestException e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
            }

            @Override
            public FileObject parseLiteral(Object input) throws CoercingParseLiteralException {
                return parseValue(input);
            }

        }).build();
    }

    @Bean
    public GraphQLScalarType orderByScalar() {
        return GraphQLScalarType.newScalar().name("OrderBy").description("排序对象, 格式如：createdAt_ASC ").coercing(new Coercing<OrderBy, String>() {
            @Override
            public String serialize(Object input) throws CoercingSerializeException {
                return input.toString();
            }

            @Override
            public OrderBy parseValue(Object input) throws CoercingParseValueException {
                String[] sort = input.toString().split("_");
                return new OrderBy(sort[0], OrderBy.Direction.valueOf(sort[1].toUpperCase()));
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

        }).build();
    }

    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar().name("Date").description(" Date 转换类").coercing(new Coercing<Date, String>() {
            @Override
            public String serialize(Object input) throws CoercingSerializeException {
                if (input instanceof Date) {
                    return DateUtil.format((Date) input, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                } else if (input instanceof String) {
                    return (String) input;
                }
                throw new CoercingSerializeException(
                    "Expected a 'String' or 'java.util.Date' but was '" + typeName(input) + "'."
                );
            }

            @Override
            public Date parseValue(Object input) throws CoercingParseValueException {
                Date date = null;
                if (input instanceof Date) {
                    date = (Date) input;
                } else if (input instanceof String) {
                    ReflectionUtils.convert(input, Date.class);
                } else {
                    throw new CoercingParseValueException(
                        "Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '" + typeName(input) + "'."
                    );
                }
                return date;
            }

            @Override
            public Date parseLiteral(Object input) throws CoercingParseLiteralException {
                if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException(
                        "Expected AST type 'StringValue' but was '" + typeName(input) + "'."
                    );
                }
                return ReflectionUtils.convert(input, Date.class);
            }

        }).build();
    }

}
