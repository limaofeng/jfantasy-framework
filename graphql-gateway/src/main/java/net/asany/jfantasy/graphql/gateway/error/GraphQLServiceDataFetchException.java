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
package net.asany.jfantasy.graphql.gateway.error;

import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.asany.jfantasy.framework.error.FieldValidationError;
import net.asany.jfantasy.graphql.client.error.DataFetchGraphQLError;

@Getter
public class GraphQLServiceDataFetchException extends GraphQLGatewayException {

  private final List<SourceLocation> locations;
  private final List<Object> path;
  private final Map<String, Object> extensions;
  private final List<FieldValidationError> fieldErrors;
  private final Map<String, Object> data;

  public GraphQLServiceDataFetchException(DataFetchGraphQLError error) {
    super(error.getMessage());
    this.locations = error.getLocations();
    this.path = error.getPath();
    this.extensions = error.getExtensions();
    this.data = error.getData();
    this.fieldErrors = error.getFieldErrors();
  }
}
