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

import graphql.language.Type;
import graphql.language.Value;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder")
public class FieldResolve {
  private String query;
  private Map<String, FieldArgument> arguments;

  @Data
  @lombok.Builder(builderClassName = "Builder")
  public static class FieldArgument {
    private String name;
    private Type<?> type;
    private boolean reference;
    private String sourceValue;
    private Value<?> value;

    public <T extends Value<?>> T getValue(Class<T> clazz) {
      return clazz.cast(value);
    }
  }
}
