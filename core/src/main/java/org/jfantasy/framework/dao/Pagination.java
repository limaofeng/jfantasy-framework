package org.jfantasy.framework.dao;

/**
 * 分页对象
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 18:22
 */
public interface Pagination {
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
   * @param totalCount
   */
  void setTotalCount(int totalCount);
}
