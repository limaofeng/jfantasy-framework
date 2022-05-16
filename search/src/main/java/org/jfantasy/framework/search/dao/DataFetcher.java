package org.jfantasy.framework.search.dao;

import java.io.Serializable;
import java.util.List;
import org.jfantasy.framework.search.backend.EntityChangedListener;

public interface DataFetcher {

  long count();

  <T> List<T> find(int start, int size);

  /**
   * @param fieldName 字段
   * @param fieldValue 字段值
   * @return List
   */
  <T> List<T> findByField(String fieldName, String fieldValue);

  <T> T getById(Serializable id);

  EntityChangedListener getListener();
}
