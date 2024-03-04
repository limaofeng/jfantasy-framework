package org.jfantasy.graphql.directives;

import graphql.Scalars;
import graphql.language.BooleanValue;
import graphql.language.StringValue;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import java.util.Arrays;
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

  private static final GraphQLArgument.Builder FORMAT_ARGUMENT =
      GraphQLArgument.newArgument()
          .name(FORMAT_NAME)
          .defaultValueLiteral(BooleanValue.of(false))
          .type(Scalars.GraphQLBoolean)
          .description("显示单位， 比如：1024 => 1 KB");

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    InputValueWithState unit = environment.getDirective().getArgument(UNIT_NAME).getArgumentValue();

    GraphQLFieldDefinition field = environment.getElement();
    GraphQLFieldsContainer parentType = environment.getFieldsContainer();
    DataFetcher<?> originalDataFetcher =
        environment.getCodeRegistry().getDataFetcher(parentType, field);

    DataFetcher<?> dataFetcher =
        DataFetcherFactories.wrapDataFetcher(
            originalDataFetcher,
            (dataFetchingEnvironment, pValue) -> {
              Long value = (Long) pValue;
              Boolean format = dataFetchingEnvironment.getArgument(FORMAT_NAME);
              if (format == null || value == null || !format) {
                return value;
              }
              double unitMultiplier =
                  Math.pow(
                      1024,
                      Arrays.binarySearch(
                          FileUtil.UNITS,
                          ((StringValue) Objects.requireNonNull(unit.getValue())).getValue()));
              return FileUtil.bytesToSize(value * Double.valueOf(unitMultiplier).longValue());
            });

    environment.getCodeRegistry().dataFetcher(parentType, field, dataFetcher);
    return field.transform(builder -> builder.argument(FORMAT_ARGUMENT));
  }
}
