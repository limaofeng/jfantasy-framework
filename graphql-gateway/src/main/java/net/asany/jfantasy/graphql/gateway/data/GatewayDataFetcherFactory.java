/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.graphql.gateway.data;

import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactory;
import graphql.schema.DataFetcherFactoryEnvironment;
import graphql.schema.DataFetchingEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.graphql.gateway.config.DataFetcherConfig;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;

@Slf4j
public class GatewayDataFetcherFactory implements DataFetcherFactory<Object> {

  private final GraphQLGatewayDataFetcher defaultDataFetcher = new GraphQLGatewayDataFetcher();

  private final DelegatedFieldResolver fieldResolver = new DelegatedFieldResolver();

  private final Map<String, DataFetcher<?>> dataFetcherMap = new HashMap<>();

  @Override
  public DataFetcher<Object> get(DataFetcherFactoryEnvironment environment) {
    return defaultDataFetcher;
  }

  public void registerDataFetcher(DataFetcherConfig dataFetcherConfig) {
    try {
      Class<?> clazz = Class.forName(dataFetcherConfig.getClassName());
      if (DataFetcher.class.isAssignableFrom(clazz)) {
        dataFetcherMap.put(
            dataFetcherConfig.getName(), (DataFetcher<?>) clazz.getConstructor().newInstance());
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      log.error("注册DataFetcher失败", e);
    } catch (InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public DataFetcher<?> getDataFetcher(String dataFetcher) {
    if (dataFetcherMap.containsKey(dataFetcher)) {
      return dataFetcherMap.get(dataFetcher);
    }
    log.warn("not found data fetcher:{}", dataFetcher);
    return defaultDataFetcher;
  }

  public DataFetcher<?> getFieldResolver() {
    return fieldResolver;
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
