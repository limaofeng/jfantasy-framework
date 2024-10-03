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
package net.asany.jfantasy.graphql.gateway.config;

import graphql.language.Value;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.graphql.gateway.util.GraphQLValueUtils;

@Builder
@Data
public class SchemaOverrideFieldArgument {
  private String name;
  private String mapping;
  private boolean exclude;
  private String value;
  private String defaultValue;
  private boolean extended;

  public boolean isFixedValue() {
    return exclude && this.value != null;
  }

  public boolean isNameChanged() {
    return !this.mapping.equals(this.name);
  }

  public Value<?> getGraphQLValue() {
    return GraphQLValueUtils.parseValue(this.value);
  }
}
