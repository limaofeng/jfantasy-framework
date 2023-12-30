package cn.asany.example.demo;

import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;

public class AppNameDataFetcher implements DataFetcher<String> {
  @Override
  public String get(DataFetchingEnvironment environment) {
    Field field = environment.getField();
    return GraphQLValueUtils.convert(
            environment.getSource(),
            StringUtil.defaultValue(field.getAlias(), field.getName()),
            environment.getFieldType(),
            environment.getGraphQlContext(),
            environment.getLocale())
        + "-xxxxx";
  }
}
