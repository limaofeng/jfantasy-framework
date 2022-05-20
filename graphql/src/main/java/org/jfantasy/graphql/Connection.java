package org.jfantasy.graphql;

import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-03 10:10
 */
public interface Connection<T> {
  PageInfo getPageInfo();

  List<T> getEdges();

  void setEdges(List<T> edges);

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
