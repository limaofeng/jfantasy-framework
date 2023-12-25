package net.asany.jfantasy.graphql.dataFetchers;

import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.graphql.context.AuthorizationGraphQLServletContext;

/**
 * GraphQL 指令配置
 *
 * @author limaofeng
 */
public class FieldPermissionDataFetcher implements DataFetcher<Object> {

  private final DataFetcher<?> dataFetcher;

  public FieldPermissionDataFetcher(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
    GraphQLFieldDefinition field = env.getElement();
    GraphQLFieldsContainer parentType = env.getFieldsContainer();
    GraphQLArgument argument = env.getDirective().getArgument("requires");

    this.dataFetcher = env.getCodeRegistry().getDataFetcher(parentType, field);
  }

  @Override
  public Object get(DataFetchingEnvironment environment) throws Exception {
    AuthorizationGraphQLServletContext context = environment.getContext();
    Authentication authentication = context.getAuthentication();

    //    if (!authentication.isAuthenticated()) {
    //      throw new Exception("You need following role: ${requiredRole}");
    //    }

    // isUnauthorized

    return fetchActualData(environment);
  }

  private Object fetchActualData(DataFetchingEnvironment environment) throws Exception {
    return dataFetcher.get(environment);
  }
}
