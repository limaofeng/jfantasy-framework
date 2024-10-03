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
package net.asany.jfantasy.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.asany.jfantasy.graphql.Connection;
import net.asany.jfantasy.graphql.Edge;
import net.asany.jfantasy.graphql.PageInfo;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2020/4/14 10:13 上午
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseConnection<E extends Edge<T>, T> implements Connection<E, T> {
  private int totalCount;
  private int pageSize;
  private int totalPage;
  private int currentPage;
  private PageInfo pageInfo;
}
