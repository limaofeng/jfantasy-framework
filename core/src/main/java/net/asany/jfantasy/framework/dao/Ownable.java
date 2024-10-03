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
package net.asany.jfantasy.framework.dao;

/**
 * 所有者标识
 *
 * @author limaofeng
 */
public interface Ownable {
  /**
   * 获取所有者
   *
   * @return 所有者
   */
  String getOwner();

  /**
   * 设置所有者
   *
   * @param owner 所有者
   */
  void setOwner(String owner);
}
