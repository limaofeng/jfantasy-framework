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

import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-03 10:10
 */
public interface Connection<E extends Edge<T>, T> {
  PageInfo getPageInfo();

  List<E> getEdges();

  void setEdges(List<E> edges);

  void setPageInfo(PageInfo pageInfo);

  @Deprecated
  void setTotalCount(int totalCount);

  @Deprecated
  void setPageSize(int pageSize);

  @Deprecated
  void setTotalPage(int totalPage);

  @Deprecated
  void setCurrentPage(int currentPage);
}
