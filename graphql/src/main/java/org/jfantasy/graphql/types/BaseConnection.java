package org.jfantasy.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jfantasy.graphql.Connection;
import org.jfantasy.graphql.PageInfo;
import org.jfantasy.graphql.Pagination;

import javax.persistence.MappedSuperclass;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/4/14 10:13 上午
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseConnection<T> implements Connection<T>, Pagination {
    private Integer totalCount;
    private Integer pageSize;
    private Integer totalPage;
    private Integer currentPage;
    private PageInfo pageInfo;
}
