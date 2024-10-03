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
package net.asany.jfantasy.framework.security.authorization.policy;

import lombok.Getter;

@Getter
public enum ResourceActionType {
  LIST("list"),
  WRITE("write"),
  READ("read");
  private final String type;

  ResourceActionType(String type) {
    this.type = type;
  }

  public static ResourceActionType of(String type) {
    for (ResourceActionType value : ResourceActionType.values()) {
      if (value.type.equalsIgnoreCase(type)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown resource action type: " + type);
  }
}
