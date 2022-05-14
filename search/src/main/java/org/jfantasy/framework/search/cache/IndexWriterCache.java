package org.jfantasy.framework.search.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.elastic.IndexWriter;

@Slf4j
public class IndexWriterCache {

  private static final IndexWriterCache instance = new IndexWriterCache();
  private final Map<Class, IndexWriter> cache;

  private IndexWriterCache() {
    this.cache = new ConcurrentHashMap<>();
  }

  public static IndexWriterCache getInstance() {
    return instance;
  }

  public IndexWriter get(Class indexedClass) {
    if (this.cache.containsKey(indexedClass)) {
      return this.cache.get(indexedClass);
    }
    synchronized (this) {
      if (this.cache.containsKey(indexedClass)) {
        return this.cache.get(indexedClass);
      }
      CuckooIndex cuckooIndex = CuckooIndex.getInstance();
      IndexWriter indexWriter = cuckooIndex.getIndexedFactory().createIndexWriter(indexedClass);
      this.cache.put(indexedClass, indexWriter);
      return this.cache.get(indexedClass);
    }
  }

  public Map<Class, IndexWriter> getAll() {
    return this.cache;
  }
}
