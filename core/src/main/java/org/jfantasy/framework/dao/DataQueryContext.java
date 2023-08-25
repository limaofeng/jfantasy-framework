package org.jfantasy.framework.dao;

import java.util.HashMap;
import java.util.Map;
import org.jfantasy.framework.dao.jpa.PropertyFilter;

public class DataQueryContext {

  private final Map<Class<?>, PropertyFilter> filterMap = new HashMap<>();

  public void addFilter(Class<?> entityClass, PropertyFilter filter) {
    this.filterMap.put(entityClass, filter);
  }

  public PropertyFilter getFilter(Class<?> entityClass) {
    return this.filterMap.get(entityClass);
  }

  public void removeFilter(Class<?> entityClass) {
    this.filterMap.remove(entityClass);
  }
}
