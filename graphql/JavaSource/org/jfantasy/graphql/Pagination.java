package org.jfantasy.graphql;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-02 18:22
 */
public interface Pagination {
    Integer getPageSize();

    Integer getTotalPage();

    Integer getCurrentPage();

    Integer getTotalCount();

    void setPageSize(Integer pageSize);

    void setTotalPage(Integer totalPage);

    void setCurrentPage(Integer currentPage);

    void setTotalCount(Integer totalCount);
}
