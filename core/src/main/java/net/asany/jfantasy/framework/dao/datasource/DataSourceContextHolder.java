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
package net.asany.jfantasy.framework.dao.datasource;

import net.asany.jfantasy.framework.util.Stack;
import net.asany.jfantasy.framework.util.common.ObjectUtil;

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
