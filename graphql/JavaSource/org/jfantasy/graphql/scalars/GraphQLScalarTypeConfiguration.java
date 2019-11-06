//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jfantasy.graphql.scalars;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.scalars.object.JsonScalar;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.graphql.util.Kit;
import org.jfantasy.storage.FileObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Date;

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
                return input instanceof FileObject ? input : input;
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
        return GraphQLScalarType.newScalar().name("OrderBy").description("排序对象, 格式如：createdAt_ASC ").coercing(new OrderCoercing()).build();
    }

    @Bean
    public GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar().name("Date").description(" Date 转换类").coercing(new Coercing<Date, Object>() {
            @Override
            public Object serialize(Object input) throws CoercingSerializeException {
                if (input instanceof Date) {
                    return ((Date) input).getTime();
                } else {
                    return input;
                }
            }

            @Override
            public Date parseValue(Object input) throws CoercingParseValueException {
                Date date = null;
                if (input instanceof Date) {
                    date = (Date) input;
                } else {
                    if (!(input instanceof String)) {
                        throw new CoercingParseValueException("Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '" + Kit.typeName(input) + "'.");
                    }

                    ReflectionUtils.convert(input, Date.class);
                }

                return date;
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
}
