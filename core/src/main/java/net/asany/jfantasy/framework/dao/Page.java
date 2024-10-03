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

import java.util.List;

/**
 * 分页对象 <br>
 * 只在 Mybatis 中使用
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 18:22
 */
public interface Page<T> {

  static <T> Page<T> of(Page<?> page, List<T> list) {
    return new Pagination<>(page, list);
  }

  static <T> Page<T> of(int currentPage, int pageSize) {
    return new Pagination<>(currentPage, pageSize, OrderBy.unsorted());
  }

  static <T> Page<T> of(int currentPage, int pageSize, OrderBy orderBy) {
    return new Pagination<>(currentPage, pageSize, orderBy);
  }

  /**
   * 每页数据条数
   *
   * @return int
   */
  int getPageSize();

  /**
   * 总页数
   *
   * @return int
   */
  int getTotalPage();

  /**
   * 当前页数
   *
   * @return int
   */
  int getCurrentPage();

  /**
   * 数据条数
   *
   * @return int
   */
  int getTotalCount();

  /**
   * 设置每页数据条数
   *
   * @param pageSize 每页数据条数
   */
  void setPageSize(int pageSize);

  /**
   * 设置总条数
   *
   * @param totalPage 总条数
   */
  void setTotalPage(int totalPage);

  /**
   * 设置当前页
   *
   * @param currentPage 当前页
   */
  void setCurrentPage(int currentPage);

  /**
   * 设置总数据条
   *
   * @param totalCount 总条数
   */
  void setTotalCount(int totalCount);

  /**
   * 返回排序字段
   *
   * @return number
   */
  OrderBy getOrderBy();

  /**
   * 开始位置
   *
   * @return number
   */
  int getOffset();

  void reset(int executeForCount);

  void reset(List<T> list);

  List<T> getPageItems();

  void setOffset(int offset);

  void setOrderBy(OrderBy orderBy);

  void reset(int totalCount, List<T> items);
}
