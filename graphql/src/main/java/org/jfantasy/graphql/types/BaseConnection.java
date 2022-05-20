package org.jfantasy.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jfantasy.graphql.Connection;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.PageInfo;

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
