package org.jfantasy.graphql.directives;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 数值格式化
 *
 * @author limaofeng
 */
public class NumberFormatDirective implements SchemaDirectiveWiring {

  private static final String FORMAT_NAME = "format";

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    GraphQLFieldDefinition field = environment.getElement();
    GraphQLFieldsContainer parentType = environment.getFieldsContainer();
    DataFetcher<?> originalDataFetcher =
        environment.getCodeRegistry().getDataFetcher(parentType, field);

    DataFetcher<?> dataFetcher =
        dataFetchingEnvironment -> {
          Object value = originalDataFetcher.get(dataFetchingEnvironment);
          String format = dataFetchingEnvironment.getArgument(FORMAT_NAME);
          if (StringUtil.isBlank(format)) {
            return value;
          }
          return value;
        };

    GraphQLArgument.Builder formatArgument =
        GraphQLArgument.newArgument()
            .name(FORMAT_NAME)
            .type(Scalars.GraphQLString)
            .description(
                "格式说明<br/>"
                    + "* 0 - (123456) 只显示整数，没有小数位<br>\n"
                    + "* 0.00 - (123456.78) 显示整数，保留两位小数位<br>\n"
                    + "* 0.0000 - (123456.7890) 显示整数，保留四位小数位<br>\n"
                    + "* 0,000 - (123,456) 只显示整数，用逗号分开<br>\n"
                    + "* 0,000.00 - (123,456.78) 显示整数，用逗号分开，保留两位小数位<br>\n"
                    + "* 0,0.00 - (123,456.78) 快捷方法，显示整数，用逗号分开，保留两位小数位<br>");

    environment.getCodeRegistry().dataFetcher(parentType, field, dataFetcher);
    return field.transform(builder -> builder.argument(formatArgument));
  }
}
