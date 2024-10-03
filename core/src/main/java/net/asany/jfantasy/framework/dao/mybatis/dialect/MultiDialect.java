/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.dao.mybatis.dialect;

import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.dao.datasource.DataSourceContextHolder;
import net.asany.jfantasy.framework.dao.datasource.DataSourceRouteProperties;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 多数据源时方言
 *
 * @author 李茂峰
 * @version 1.0 当使用@DataSource区分数据库时，用于匹配不同的数据源
 * @since 2012-10-28 下午08:16:54
 */
public class MultiDialect implements Dialect, InitializingBean {

  private Map<String, Dialect> targetDialects;

  private Map<String, String> dataSourceTypes;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (ObjectUtil.isNull(targetDialects)) {
      targetDialects = new HashMap<>();
    }
    targetDialects.put("sql2005", new MsSQLDialect("2005"));
    targetDialects.put("sql2000", new MsSQLDialect("2000"));
    targetDialects.put("db2", new DB2SQLDialect());
    targetDialects.put("oracle", new OraSQLDialect());
    targetDialects.put("mysql", new MySQLDialect());
  }

  @Override
  public String getCountString(String sql) {
    return getDialect().getCountString(sql);
  }

  private Dialect getDialect() {
    DataSourceRouteProperties dataSource = DataSourceContextHolder.getDataSource();
    Assert.notNull(dataSource, "SqlMapper未指定@DataSource,无法确定翻页方言");
    return targetDialects.get(dataSourceTypes.get(dataSource.getName()));
  }

  @Override
  public String getLimitString(String sql, int offset, int limit) {
    return getDialect().getLimitString(sql, offset, limit);
  }

  public void setTargetDialects(Map<String, Dialect> targetDialects) {
    this.targetDialects = targetDialects;
  }

  public void setDataSourceTypes(Map<String, String> dataSourceTypes) {
    this.dataSourceTypes = dataSourceTypes;
  }
}
