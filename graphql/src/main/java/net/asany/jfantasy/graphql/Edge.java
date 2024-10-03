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
package net.asany.jfantasy.graphql;

/**
 * 边
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 18:23
 */
public interface Edge<T> {
  /**
   * 游标
   *
   * @return String
   */
  String getCursor();

  /**
   * 当前数据
   *
   * @return T
   */
  T getNode();

  /**
   * 设置游标
   *
   * @param cursor String
   */
  void setCursor(String cursor);

  /**
   * 设置数据
   *
   * @param node T
   */
  void setNode(T node);
}
