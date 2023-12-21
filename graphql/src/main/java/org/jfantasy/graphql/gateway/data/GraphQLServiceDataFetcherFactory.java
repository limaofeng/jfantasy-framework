package org.jfantasy.graphql.gateway.data;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactory;
import graphql.schema.DataFetcherFactoryEnvironment;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.graphql.client.GraphQLResponse;
import org.jfantasy.graphql.client.GraphQLTemplate;
import org.jfantasy.graphql.gateway.error.DataFetchGraphQLError;
import org.jfantasy.graphql.gateway.error.GraphQLServiceDataFetchException;
import org.jfantasy.graphql.gateway.error.GraphQLServiceNetworkException;
import org.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import org.jfantasy.graphql.util.GraphQLUtils;
import org.jfantasy.graphql.util.GraphQLValueUtils;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
public class GraphQLServiceDataFetcherFactory implements DataFetcherFactory<Object> {

  private final RemoteGraphQLService service;

  public GraphQLServiceDataFetcherFactory(RemoteGraphQLService service) {
    this.service = service;
  }

  @Override
  public DataFetcher<Object> get(DataFetcherFactoryEnvironment environment) {
    return new GraphQLServiceDataFetcher(service);
  }

  public static class GraphQLServiceDataFetcher implements DataFetcher<Object> {

    private final RemoteGraphQLService service;

    public GraphQLServiceDataFetcher(RemoteGraphQLService service) {
      this.service = service;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
      String parentType = ClassUtil.getValue(environment.getParentType(), "name");
      String operationName = environment.getOperationDefinition().getName();
      Field field = environment.getField();

      if (!"Query".equals(parentType)) {
        log.debug("忽略非查询类型的字段:{}", parentType + "." + field.getName());
        return GraphQLValueUtils.convert(
            environment.getSource(),
            StringUtil.defaultValue(field.getAlias(), field.getName()),
            environment.getFieldType(),
            environment.getGraphQlContext(),
            environment.getLocale());
      }

      environment.getGraphQlContext().put(GraphQLUtils.ENV_CURRENT_SERVICE, service);

      String gql = GraphQLUtils.buildGraphQLQuery(environment);

      log.warn("gql:" + gql);

      GraphQLTemplate client = this.service.getClient();
      GraphQLResponse response;

      try {
        response = client.post(gql, operationName, environment.getVariables());
      } catch (ResourceAccessException e) {
        throw new GraphQLServiceNetworkException(e.getMessage());
      }

      if (!response.isOk()) {
        throw new GraphQLServiceNetworkException(response.getRawResponse().getBody());
      }
      List<DataFetchGraphQLError> errors =
          response.getList("$.errors", DataFetchGraphQLError.class);
      if (errors != null && !errors.isEmpty()) {
        if (errors.size() > 1) {
          log.warn("* TODO: 逻辑漏洞 errors.size() > 1");
        }
        throw new GraphQLServiceDataFetchException(errors.get(0));
      }

      if (response.get("$.data", JsonNode.class) == null) {
        return null;
      }

      return response.get(
          "$.data." + StringUtil.defaultValue(field.getAlias(), field.getName()), JsonNode.class);
    }
  }
}
