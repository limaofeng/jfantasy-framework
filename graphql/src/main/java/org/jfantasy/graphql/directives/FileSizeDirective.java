package org.jfantasy.graphql.directives;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import java.time.format.DateTimeFormatter;
import org.jfantasy.framework.util.common.file.FileUtil;

/**
 * FileSize 指令
 *
 * @author limaofeng
 */
public class FileSizeDirective implements SchemaDirectiveWiring {

  private static final String UNIT_NAME = "unit";
  private static final String FORMAT_NAME = "format";

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    String unit = (String) environment.getDirective().getArgument(UNIT_NAME).getValue();

    GraphQLFieldDefinition field = environment.getElement();
    GraphQLFieldsContainer parentType = environment.getFieldsContainer();
    DataFetcher<?> originalDataFetcher =
        environment.getCodeRegistry().getDataFetcher(parentType, field);

    DataFetcher<?> dataFetcher =
        dataFetchingEnvironment -> {
          Long value = (Long) originalDataFetcher.get(dataFetchingEnvironment);
          boolean format = dataFetchingEnvironment.getArgument(FORMAT_NAME);
          if (!format) {
            return value;
          }
          return FileUtil.fileSize(FileUtil.fileSize(value, unit));
        };

    GraphQLArgument.Builder formatArgument =
        GraphQLArgument.newArgument()
            .name(FORMAT_NAME)
            .type(Scalars.GraphQLBoolean)
            .description("显示单位， 比如：1024 => 1 KB");

    environment.getCodeRegistry().dataFetcher(parentType, field, dataFetcher);
    return field.transform(builder -> builder.argument(formatArgument));
  }

  private DateTimeFormatter buildFormatter(String format) {
    String dtFormat = format != null ? format : "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";
    return DateTimeFormatter.ofPattern(dtFormat);
  }
}
