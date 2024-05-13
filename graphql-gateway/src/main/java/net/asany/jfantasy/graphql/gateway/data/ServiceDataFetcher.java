package net.asany.jfantasy.graphql.gateway.data;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.ognl.OgnlUtil;
import net.asany.jfantasy.graphql.client.GraphQLResponse;
import net.asany.jfantasy.graphql.client.QueryPayload;
import net.asany.jfantasy.graphql.gateway.GraphQLClient;
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

    if (!"Query".equals(parentType.getName())
        && !"Mutation".equals(parentType.getName())
        && !"Subscription".equals(parentType.getName())) {
      log.debug("忽略非查询类型的字段:{}", parentType.getName() + "." + field.getName());
      return GraphQLValueUtils.convert(
          environment.getSource(),
          StringUtil.defaultValue(field.getAlias(), field.getName()),
          environment.getFieldType(),
          environment.getGraphQlContext(),
          environment.getLocale());
    }

    String gql = GraphQLUtils.buildGraphQLQuery(environment);

    Map<String, Object> variables = GraphQLUtils.buildVariables(environment, getSchema());

    log.debug("GraphQL Query: {}", gql);
    log.debug("GraphQL Variables: {}", variables);

    GraphQLClient client = this.service.getClient();
    GraphQLResponse response;

    // 获取 token
    String token = null;
    AuthenticationToken authenticationToken = environment.getGraphQlContext().get("authentication");
    if (authenticationToken
        instanceof BearerTokenAuthenticationToken bearerTokenAuthenticationToken) {
      token = bearerTokenAuthenticationToken.getToken();
    }

    QueryPayload payload =
        QueryPayload.builder().query(gql).operationName(operationName).variables(variables).build();

    // 处理订阅
    if ("Subscription".equals(parentType.getName())) {
      OgnlUtil ognlUtil = OgnlUtil.getInstance();
      //noinspection ReactiveStreamsUnusedPublisher
      return client.subscribe(
          payload,
          token,
          data -> {
            List<Object> errors = ognlUtil.getValue("errors", data);
            if (errors != null) {
              List<DataFetchGraphQLError> errorsList =
                  JSON.getObjectMapper()
                      .convertValue(
                          errors,
                          JSON.getObjectMapper()
                              .getTypeFactory()
                              .constructCollectionType(List.class, DataFetchGraphQLError.class));
              throw new GraphQLServiceDataFetchException(errorsList.get(0));
            }
            Object result = ognlUtil.getValue("data", data);
            JsonNode jsonNode = JSON.getObjectMapper().valueToTree(result);

            return GraphQLValueUtils.convert(
                jsonNode,
                StringUtil.defaultValue(field.getAlias(), field.getName()),
                environment.getFieldType(),
                environment.getGraphQlContext(),
                environment.getLocale());
          });
    }

    try {
      response = client.query(payload, token);
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

    JsonNode result = response.get("$.data", JsonNode.class);

    if (result == null) {
      return null;
    }

    return GraphQLValueUtils.convert(
        result,
        StringUtil.defaultValue(field.getAlias(), field.getName()),
        environment.getFieldType(),
        environment.getGraphQlContext(),
        environment.getLocale());
  }

  public GraphQLSchema getSchema() {
    return this.service.getSchema();
  }
}
