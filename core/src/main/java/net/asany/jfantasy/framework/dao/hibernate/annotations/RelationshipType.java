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
package net.asany.jfantasy.framework.dao.hibernate.annotations;

import lombok.Getter;

@Getter
public enum RelationshipType {
  /**
   * 隶属于 <br>
   * 描述：实体直接归属于另一个实体。这个关系最常用，例如设备属于店铺，店铺属于客户 <br>
   * 示例：(设备 1, 店铺 2, 'belongs_to')，表示设备 1 属于店铺 2。
   */
  BELONGS_TO("belongs_to"),
  /**
   * 被拥有 <br>
   * 描述：实体被另一个实体拥有。这个和 belongs_to 类似，但语义上更强调所有权，可以在一些需要清晰描述所有权的场景使用。 <br>
   * 示例：(店铺 2, 客户 3, 'owned_by')，表示店铺 2 被客户 3 拥有。
   */
  OWNED_BY("owned_by"),
  /**
   * 由...管理 <br>
   * 描述：表示实体由另一个实体进行管理。这通常用在例如店铺由某个客户或管理公司进行管理的场景。 <br>
   * 示例：(设备 1, 店铺 2, 'managed_by')，表示设备 1 由店铺 2 管理。
   */
  MANAGED_BY("managed_by"),
  /**
   * 位于 描述：表示实体的物理或逻辑位置。<br>
   * 例如，设备位于某个店铺、店铺位于某个地区等。 示例：(设备 1, 店铺 2, 'located_in')，<br>
   * 表示设备 1 位于店铺 2。
   */
  LOCATED_IN("located_in"),
  /**
   * 汇报给 <br>
   * 描述：用于描述从属关系，例如某个店铺向某个客户汇报。这可以用于表达更抽象的管理关系。 <br>
   * 示例：(店铺 2, 客户 3, 'reports_to')，表示店铺 2 汇报给客户 3。
   */
  REPORTS_TO("reports_to"),
  /**
   * 属于...的一部分 <br>
   * 描述：表示实体是另一个实体的一部分，强调整体与部分的关系。类似于 "belongs_to"，但更强调一个整体-部分的关系，<br>
   * 例如某个设备是一个更大系统的一部分。 示例：(设备 1, 系统 A, 'part_of')，表示设备 1 是系统 A 的一部分。
   */
  PART_OF("part_of"),
  /**
   * 使用 <br>
   * 描述：表示实体使用另一个实体。适用于一些特定场景，例如店铺使用某种设备，设备使用某种软件。 <br>
   * 示例：(店铺 2, 设备 1, 'uses')，表示店铺 2 使用设备 1。
   */
  USES("uses"),
  /**
   * 出售 <br>
   * 描述：用于表示商业关系，例如店铺出售某种设备。 <br>
   * 示例：(店铺 2, 设备 1, 'sells')，表示店铺 2 出售设备 1。
   */
  SELLS("sells"),
  /**
   * 分配给 <br>
   * 描述：用于描述资源分配关系，例如某个设备被分配给某个店铺或员工。 <br>
   * 示例：(设备 1, 店铺 2, 'assigned_to')，表示设备 1 被分配给店铺 2。
   */
  ASSIGNED_TO("assigned_to"),
  /**
   * 租赁 <br>
   * 描述：表示租赁关系，例如设备租赁给某个店铺。 <br>
   * 示例：(店铺 2, 设备 1, 'rents')，表示店铺 2 租赁了设备 1。
   */
  RENTS("rents"),
  /**
   * 监控 <br>
   * 描述：表示监控关系，例如某个客户监控多个店铺的运行状态。 <br>
   * 示例：(客户 3, 店铺 2, 'monitors')，表示客户 3 监控店铺 2。
   */
  MONITORS("monitors"),
  /**
   * 包含 <br>
   * 描述：表示包含关系，例如某个客户包含多个店铺，或者一个店铺包含多个设备。与 part_of 相对应。 <br>
   * 示例：(客户 3, 店铺 2, 'contains')，表示客户 3 包含店铺 2。
   */
  CONTAINS("contains");

  // Getter for the string value
  private final String value;

  // Constructor
  RelationshipType(String value) {
    this.value = value;
  }

  // Static method to get enum from string
  public static RelationshipType fromValue(String value) {
    for (RelationshipType relationship : RelationshipType.values()) {
      if (relationship.value.equalsIgnoreCase(value)) {
        return relationship;
      }
    }
    throw new IllegalArgumentException("Unknown relationship: " + value);
  }
}
