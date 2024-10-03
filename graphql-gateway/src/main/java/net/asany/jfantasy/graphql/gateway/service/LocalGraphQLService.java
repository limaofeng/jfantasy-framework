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
package net.asany.jfantasy.graphql.gateway.service;

import graphql.schema.GraphQLSchema;
import java.util.List;
import lombok.Builder;

@Builder(builderClassName = "Builder")
public class LocalGraphQLService implements GraphQLService {

  private final GraphQLSchema schema;

  private List<String> excludeFields;

  @Override
  public String getName() {
    return "local";
  }

  @Override
  public GraphQLSchema makeSchema() {
    return schema;
  }

  public static class Builder {

    public Builder excludeFields(List<String> fields) {
      this.excludeFields = fields;
      return this;
    }

    public Builder excludeFields(String... fields) {
      this.excludeFields = List.of(fields);
      return this;
    }
  }
}
