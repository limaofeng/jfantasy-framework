package net.asany.jfantasy.framework.dao.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多数据源情况下，用于标示对应的数据源
 *
 * @author limaofeng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataSourceRouteProperties {
  private String name;
  private String catalog;
}
