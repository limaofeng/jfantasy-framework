package org.jfantasy.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 分页信息
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 18:24
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
  private Boolean hasNextPage;
  private Boolean hasPreviousPage;
  private String startCursor;
  private String endCursor;
  /** 当前页码 */
  private int current;
  /** 每页条数 */
  private int pageSize;
  /** 数据总数 */
  private long total;
  /** 总页数 */
  private int totalPages;
}
