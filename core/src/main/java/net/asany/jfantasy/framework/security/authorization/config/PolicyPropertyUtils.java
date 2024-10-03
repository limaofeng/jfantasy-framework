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
package net.asany.jfantasy.framework.security.authorization.config;

import net.asany.jfantasy.framework.security.authorization.policy.PermissionStatement;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class PolicyPropertyUtils extends PropertyUtils {
  @Override
  public Property getProperty(Class<?> type, String name) {
    if (type == AuthorizationConfiguration.class && "default".equals(name)) {
      name = "defaultPolicy";
    }
    if (type == AuthorizationConfiguration.class && "public-paths".equals(name)) {
      name = "publicPaths";
    }
    if (type == PermissionStatement.class && "condition".equals(name)) {
      name = "conditions";
    }
    return super.getProperty(type, name);
  }
}
