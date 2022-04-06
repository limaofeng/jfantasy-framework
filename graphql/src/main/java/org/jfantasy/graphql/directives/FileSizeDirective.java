package org.jfantasy.graphql.directives;

import graphql.Scalars;
import graphql.language.BooleanValue;
import graphql.language.StringValue;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import java.util.Objects;
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
    InputValueWithState unit = environment.getDirective().getArgument(UNIT_NAME).getArgumentValue();

    GraphQLFieldDefinition field = environment.getElement();
    GraphQLFieldsContainer parentType = environment.getFieldsContainer();
    DataFetcher<?> originalDataFetcher =
        environment.getCodeRegistry().getDataFetcher(parentType, field);

    DataFetcher<?> dataFetcher =
        dataFetchingEnvironment -> {
          Long value = (Long) originalDataFetcher.get(dataFetchingEnvironment);
          Boolean format = dataFetchingEnvironment.getArgument(FORMAT_NAME);
          if (format == null || value == null || !format) {
            return value;
          }
          return FileUtil.fileSize(
              FileUtil.fileSize(
                  value, ((StringValue) Objects.requireNonNull(unit.getValue())).getValue()));
        };

    GraphQLArgument.Builder formatArgument =
        GraphQLArgument.newArgument()
            .name(FORMAT_NAME)
            .defaultValueLiteral(BooleanValue.of(false))
            .type(Scalars.GraphQLBoolean)
            .description("显示单位， 比如：1024 => 1 KB");

    environment.getCodeRegistry().dataFetcher(parentType, field, dataFetcher);
    return field.transform(builder -> builder.argument(formatArgument));
  }
}
