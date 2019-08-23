package org.jfantasy.graphql.directives;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-14 12:40
 */
public class DateFormatDirective implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        DataFetcher dataFetcher = DataFetcherFactories.wrapDataFetcher(field.getDataFetcher(), ((dataFetchingEnvironment, value) -> {
            DateTimeFormatter dateTimeFormatter = buildFormatter(dataFetchingEnvironment.getArgument("format"));
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
                .defaultValue("YYYY-MM-dd'T'HH:mm:ss.SSS'Z'")
            )
            .dataFetcher(dataFetcher)
        );
    }

    private DateTimeFormatter buildFormatter(String format) {
        String dtFormat = format != null ? format : "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";
        return DateTimeFormatter.ofPattern(dtFormat);
    }
}
