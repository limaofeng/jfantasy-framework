package org.jfantasy.framework.dao.datasource;

import org.jfantasy.framework.util.Stack;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * 多数据源管理类
 *
 * @author limaofeng
 */
public class DataSourceContextHolder {

  private static final ThreadLocal<DataSourceContextHolder> DELEGATE = new ThreadLocal<>();

  private final Stack<DataSourceRouteProperties> stack = new Stack<>();

  public static DataSourceContextHolder getContext() {
    DataSourceContextHolder localMessage = DELEGATE.get();
    if (ObjectUtil.isNull(localMessage)) {
      DELEGATE.set(new DataSourceContextHolder());
    }
    return DELEGATE.get();
  }

  public static DataSourceRouteProperties getDataSource() {
    return getContext().peek();
  }

  public static void addDataSourceRoute(String dataSourceKey) {
    getContext().push(DataSourceRouteProperties.builder().name(dataSourceKey).build());
  }

  public static void addDataSourceRoute(String dataSourceKey, String catalog) {
    getContext()
        .push(DataSourceRouteProperties.builder().name(dataSourceKey).catalog(catalog).build());
  }

  public static void removeDataSourceRoute() {
    getContext().pop();
  }

  private void push(DataSourceRouteProperties dataSource) {
    this.stack.push(dataSource);
  }

  private DataSourceRouteProperties peek() {
    return this.stack.peek();
  }

  private DataSourceRouteProperties pop() {
    return this.stack.pop();
  }

  public void clear() {
    DELEGATE.remove();
  }
}
