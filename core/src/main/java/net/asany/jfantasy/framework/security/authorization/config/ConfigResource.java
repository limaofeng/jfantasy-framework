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

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceActionType;

@Data
public class ConfigResource {
  private String id;
  private String service;
  private String description;
  private List<ConfigResourceAction> actions;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ConfigResourceAction implements ResourceAction {
    private String id;
    private ResourceActionType type;
    private String description;
    private List<String> operations;
    private Set<String> arn;
  }
}
