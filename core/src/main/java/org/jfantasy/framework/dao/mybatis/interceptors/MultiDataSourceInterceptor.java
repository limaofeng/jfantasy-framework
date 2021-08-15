package org.jfantasy.framework.dao.mybatis.interceptors;

import java.util.Properties;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.jfantasy.framework.dao.MultiDataSourceManager;
import org.jfantasy.framework.dao.annotations.DataSource;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;

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
            ClassUtil.forName(RegexpUtil.replace(ms.getId(), ".[_a-zA-Z0-9]+$", "")),
            DataSource.class);
    try {
      if (ObjectUtil.isNotNull(dataSource)) {
        MultiDataSourceManager.getManager().push(dataSource);
      }
      return invocation.proceed();
    } finally {
      if (ObjectUtil.isNotNull(dataSource)) {
        MultiDataSourceManager.getManager().pop();
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
