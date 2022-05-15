package org.jfantasy.framework.search.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.exception.NoIndexException;

@Slf4j
public class IndexCache {

  private static final IndexCache instance = new IndexCache();
  private final Map<Class, CuckooIndex> cache;

  private IndexCache() {
    this.cache = new ConcurrentHashMap<>();
  }

  public static IndexCache getInstance() {
    return instance;
  }

  public CuckooIndex get(Class indexedClass) {
    if (this.cache.containsKey(indexedClass)) {
      throw new NoIndexException(indexedClass.getName() + "  索引未找到");
    }
    return this.cache.get(indexedClass);
  }

  public void put(Class indexClass, CuckooIndex index) {
    this.cache.put(indexClass, index);
  }

  public Map<Class, CuckooIndex> getAll() {
    return this.cache;
  }
}
