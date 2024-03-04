package org.jfantasy.framework.dao.datasource;

import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多数据源事件
 *
 * @author limaofeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiDataSourceEvent {
  private String name;
  private DataSource dataSource;
}
