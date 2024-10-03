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
package net.asany.jfantasy.framework.dao.mybatis.interceptors;

import java.util.Objects;
import java.util.Properties;
import net.asany.jfantasy.framework.dao.annotations.DataSource;
import net.asany.jfantasy.framework.dao.datasource.DataSourceContextHolder;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;

/**
 * 多数据源拦截器
 *
 * @author 李茂峰
 * @version 1.0 提取Mapper接口上的@DataSource信息
 * @since 2012-10-28 下午07:58:04
 */
@Intercepts({
  @org.apache.ibatis.plugin.Signature(
      type = org.apache.ibatis.executor.Executor.class,
      method = "query",
      args = {
        MappedStatement.class,
        Object.class,
        org.apache.ibatis.session.RowBounds.class,
        org.apache.ibatis.session.ResultHandler.class
      }),
  @org.apache.ibatis.plugin.Signature(
      type = org.apache.ibatis.executor.Executor.class,
      method = "query",
      args = {
        MappedStatement.class,
        Object.class,
        org.apache.ibatis.session.RowBounds.class,
        org.apache.ibatis.session.ResultHandler.class,
        org.apache.ibatis.cache.CacheKey.class,
        org.apache.ibatis.mapping.BoundSql.class
      }),
  @org.apache.ibatis.plugin.Signature(
      type = org.apache.ibatis.executor.Executor.class,
      method = "update",
      args = {MappedStatement.class, Object.class})
})
public class MultiDataSourceInterceptor implements Interceptor {
  static int MAPPED_STATEMENT_INDEX = 0;

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    MappedStatement ms = (MappedStatement) invocation.getArgs()[MAPPED_STATEMENT_INDEX];
    DataSource dataSource =
        ClassUtil.getClassGenricType(
            Objects.requireNonNull(
                ClassUtil.forName(RegexpUtil.replace(ms.getId(), ".[_a-zA-Z0-9]+$", ""))),
            DataSource.class);
    try {
      if (ObjectUtil.isNotNull(dataSource)) {
        DataSourceContextHolder.addDataSourceRoute(dataSource.name(), dataSource.catalog());
      }
      return invocation.proceed();
    } finally {
      if (ObjectUtil.isNotNull(dataSource)) {
        DataSourceContextHolder.removeDataSourceRoute();
      }
    }
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {}
}
