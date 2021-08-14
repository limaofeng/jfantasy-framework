package org.jfantasy.graphql.directives;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2019-04-14 12:40
 */
public class DateFormatDirective implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        DataFetcher originalDataFetcher = ClassUtil.call("getDataFetcher", field);
        DataFetcher dataFetcher = DataFetcherFactories.wrapDataFetcher(originalDataFetcher, ((dataFetchingEnvironment, value) -> {
            String format = dataFetchingEnvironment.getArgument("format");
            if (StringUtil.isBlank(format)) {
                if (value instanceof LocalDateTime) {
                    return Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant()).getTime();
                } else if (value instanceof Date) {
                    return value;
                } else {
                    return value;
                }
            }
            DateTimeFormatter dateTimeFormatter = buildFormatter(format);
            if (value instanceof LocalDateTime) {
                return dateTimeFormatter.format((LocalDateTime) value);
            } else if (value instanceof Date) {
                return dateTimeFormatter.format(LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()));
            }
            return value;
        }));
        return field.transform(builder -> builder
            .argument(GraphQLArgument
                .newArgument()
                .name("format")
                .type(Scalars.GraphQLString)
            )
            .dataFetcher(dataFetcher)
        );
    }

    private DateTimeFormatter buildFormatter(String format) {
        String dtFormat = format != null ? format : "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";
        return DateTimeFormatter.ofPattern(dtFormat);
    }
}
