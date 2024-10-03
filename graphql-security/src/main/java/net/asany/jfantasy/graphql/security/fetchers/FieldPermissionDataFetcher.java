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
package net.asany.jfantasy.graphql.security.fetchers;

import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.graphql.security.context.AuthGraphQLServletContext;

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
    AuthGraphQLServletContext context = environment.getContext();
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
