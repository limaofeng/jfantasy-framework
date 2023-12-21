package org.jfantasy.graphql.gateway.data;

import graphql.language.Field;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.graphql.util.GraphQLValueUtils;

@Slf4j
public class GraphQLGatewayDataFetcherFactory implements DataFetcherFactory<Object> {

  private final GraphQLGatewayDataFetcher defaultDataFetcher = new GraphQLGatewayDataFetcher();

  @Override
  public DataFetcher<Object> get(DataFetcherFactoryEnvironment environment) {
    return defaultDataFetcher;
  }

  public static class GraphQLGatewayDataFetcher implements DataFetcher<Object> {

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
      Field field = environment.getField();
      String parentType = ClassUtil.getValue(environment.getParentType(), "name");

      if (!"Query".equals(parentType)) {
        log.debug("忽略非查询类型的字段:{}", parentType + "." + field.getName());
        return GraphQLValueUtils.convert(
            environment.getSource(),
            StringUtil.defaultValue(field.getAlias(), field.getName()),
            environment.getFieldType(),
            environment.getGraphQlContext(),
            environment.getLocale());
      }

      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
