package org.jfantasy.framework.search.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jfantasy.framework.search.dao.DataFetcher;

public class DaoCache {

  private static final DaoCache instance = new DaoCache();
  private final Map<Class<?>, DataFetcher> cache;

  private DaoCache() {
    this.cache = new ConcurrentHashMap<>();
  }

  public static DaoCache getInstance() {
    return instance;
  }

  public boolean containsKey(Class<?> clazz) {
    return this.cache.containsKey(clazz);
  }

  public <T> DataFetcher get(Class<T> clazz) {
    return this.cache.get(clazz);
  }

  public <T> void put(Class<T> clazz, DataFetcher dao) {
    this.cache.put(clazz, dao);
  }
}
