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
package net.asany.jfantasy.graphql.gateway.type;

import graphql.schema.GraphQLScalarType;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;

@Slf4j
public class ExtendedScalarsTypeProvider implements ScalarTypeProvider {

  @Override
  public String getName() {
    return "extended-scalars";
  }

  @Override
  public GraphQLScalarType getScalarType(GatewayConfig.ScalarConfig config) {
    String name = Optional.ofNullable(config.getResolver()).orElse(config.getName());
    try {
      return (GraphQLScalarType)
          Class.forName("graphql.scalars.ExtendedScalars").getField(name).get(null);
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
