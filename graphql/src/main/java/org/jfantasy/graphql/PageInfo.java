package org.jfantasy.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 18:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
  private Boolean hasNextPage;
  private Boolean hasPreviousPage;
  private String startCursor;
  private String endCursor;
}
