package org.jfantasy.framework.search.dao.jpa;

import java.util.List;
import org.jfantasy.framework.search.backend.EntityChangedListener;
import org.jfantasy.framework.search.dao.DataFetcher;

public class JpaDefaultDataFetcher implements DataFetcher {
  @Override
  public long count() {
    return 0;
  }

  @Override
  public <T> List<T> find(int start, int size) {
    return null;
  }

  @Override
  public <T> List<T> findByField(String fieldName, String fieldValue) {
    return null;
  }

  @Override
  public <T> T getById(String id) {
    return null;
  }

  @Override
  public EntityChangedListener getListener() {
    return null;
  }
}
