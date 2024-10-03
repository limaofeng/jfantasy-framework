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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.util.common.StringUtil;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResource {
  private String tenantId;
  private String serviceName;
  private String resourceType;
  private String resourceId;

  private String arn;
  private String path;

  public boolean appliesToResource(String resourceString) {
    PermissionResource input = PermissionResource.parse(resourceString);
    return appliesToTenant(input.tenantId)
        && appliesToService(input.serviceName)
        && appliesToResourceType(input.resourceType)
        && appliesToResourceId(input.resourceId);
  }

  public boolean appliesToTenant(String tenantId) {
    if (StringUtil.isBlank(this.tenantId) || "*".equals(this.tenantId)) {
      return true;
    }
    return this.tenantId.equals(tenantId);
  }

  public boolean appliesToService(String serviceName) {
    if (StringUtil.isBlank(this.serviceName) || "*".equals(this.serviceName)) {
      return true;
    }
    return this.serviceName.equals(serviceName);
  }

  public boolean appliesToResourceType(String resourceType) {
    if (StringUtil.isBlank(this.resourceType) || "*".equals(this.resourceType)) {
      return true;
    }
    return this.resourceType.equals(resourceType);
  }

  public boolean appliesToResourceId(String resourceId) {
    if (StringUtil.isBlank(this.resourceType) || "*".equals(this.resourceType)) {
      return true;
    }
    return this.resourceId.equals(resourceId);
  }

  public static PermissionResource parse(String resource) {
    PermissionResource.Builder resourceBuilder = PermissionResource.builder();
    if (!resource.startsWith("arn:")) {
      String[] parts = resource.split("/");
      String resourceType = parts[0];
      String resourceId = parts.length > 1 ? parts[1] : "*";
      return resourceBuilder
          .resourceId("*")
          .serviceName("*")
          .tenantId("*")
          .resourceType(resourceType)
          .resourceId(resourceId)
          .path(resource)
          .build();
    }
    int lastIndex = resource.lastIndexOf(":");
    String path = resource.substring(lastIndex + 1);
    String[] parts = resource.split(":");
    resourceBuilder.path(path).serviceName(parts[2]).tenantId(parts[1]);
    parts = path.split("/");
    String resourceType = parts[0];
    String resourceId = parts.length > 1 ? parts[1] : "*";
    return resourceBuilder.resourceType(resourceType).resourceId(resourceId).build();
  }
}
