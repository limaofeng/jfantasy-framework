package org.jfantasy.graphql.directives;

import graphql.Scalars;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 日期格式化
 *
 * @author limaofeng
 * @version V1.0
 */
public class DateFormatDirective implements SchemaDirectiveWiring {

  private static final String FORMAT_NAME = "format";

  private static final GraphQLArgument.Builder FORMAT_ARGUMENT =
      GraphQLArgument.newArgument()
          .name(FORMAT_NAME)
          .type(Scalars.GraphQLString)
          .description("日期格式, 如： YYYY-MM-dd");

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    GraphQLFieldDefinition field = environment.getElement();
    GraphQLFieldsContainer parentType = environment.getFieldsContainer();
    DataFetcher<?> originalDataFetcher =
        environment.getCodeRegistry().getDataFetcher(parentType, field);

    DataFetcher<?> dataFetcher =
        DataFetcherFactories.wrapDataFetcher(
            originalDataFetcher,
            (dataFetchingEnvironment, value) -> {
              String format = dataFetchingEnvironment.getArgument(FORMAT_NAME);
              if (StringUtil.isBlank(format)) {
                if (value instanceof LocalDateTime) {
                  return Date.from(
                          ((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant())
                      .getTime();
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
                return dateTimeFormatter.format(
                    LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()));
              }
              return value;
            });

    environment.getCodeRegistry().dataFetcher(parentType, field, dataFetcher);
    return field.transform(builder -> builder.argument(FORMAT_ARGUMENT));
  }

  private DateTimeFormatter buildFormatter(String format) {
    String dtFormat = format != null ? format : "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";
    return DateTimeFormatter.ofPattern(dtFormat);
  }
}
