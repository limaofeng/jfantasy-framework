package net.asany.jfantasy.graphql.gateway.data;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.graphql.client.GraphQLResponse;
import net.asany.jfantasy.graphql.client.GraphQLTemplate;
import net.asany.jfantasy.graphql.gateway.error.DataFetchGraphQLError;
import net.asany.jfantasy.graphql.gateway.error.GraphQLServiceDataFetchException;
import net.asany.jfantasy.graphql.gateway.error.GraphQLServiceNetworkException;
import net.asany.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import net.asany.jfantasy.graphql.gateway.util.GraphQLUtils;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
public class ServiceDataFetcher implements DataFetcher<Object> {

  private final RemoteGraphQLService service;

  public ServiceDataFetcher(RemoteGraphQLService service) {
    this.service = service;
  }

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    GraphQLObjectType parentType = (GraphQLObjectType) environment.getParentType();
    String operationName = environment.getOperationDefinition().getName();
    Field field = environment.getField();

    if (!"Query".equals(parentType.getName()) && !"Mutation".equals(parentType.getName())) {
      log.debug("忽略非查询类型的字段:{}", parentType.getName() + "." + field.getName());
      return GraphQLValueUtils.convert(
          environment.getSource(),
          StringUtil.defaultValue(field.getAlias(), field.getName()),
          environment.getFieldType(),
          environment.getGraphQlContext(),
          environment.getLocale());
    }

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
    List<DataFetchGraphQLError> errors = response.getList("$.errors", DataFetchGraphQLError.class);
    if (errors != null && !errors.isEmpty()) {
      if (errors.size() > 1) {
        log.warn("* TODO: 逻辑漏洞 errors.size() > 1");
      }
      throw new GraphQLServiceDataFetchException(errors.get(0));
    }

    if (response.get("$.data", JsonNode.class) == null) {
      return null;
    }
    String resultKey = field.getAlias() != null ? field.getAlias() : field.getName();
    return response.get("$.data." + resultKey, JsonNode.class);
  }

  public GraphQLSchema getSchema() {
    return this.service.getSchema();
  }
}
