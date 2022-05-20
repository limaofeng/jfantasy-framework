package org.jfantasy.framework.dao;

import java.util.List;

/**
 * 分页对象
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 18:22
 */
@Deprecated
public interface Page<T> {
  static <T> Page<T> of(Page page, List<T> list) {
    return new Pagination<>(page, list);
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
   * @param pageSize
   */
  void setPageSize(int pageSize);

  /**
   * 设置同条数
   *
   * @param totalPage
   */
  void setTotalPage(int totalPage);

  /**
   * 设置当前页
   *
   * @param currentPage
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
