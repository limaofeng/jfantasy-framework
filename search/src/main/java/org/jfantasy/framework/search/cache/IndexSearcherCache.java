package org.jfantasy.framework.search.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfantasy.framework.search.elastic.IndexSearcher;

public class IndexSearcherCache {

  private static final Logger LOGGER = LogManager.getLogger(IndexSearcherCache.class);

  private static IndexSearcherCache instance = new IndexSearcherCache();
  private Map<String, IndexSearcher> cache;

  private IndexSearcherCache() {
    this.cache = new ConcurrentHashMap<String, IndexSearcher>();
  }

  public static IndexSearcherCache getInstance() {
    return instance;
  }

  public IndexSearcher get(String name) {
    IndexSearcher searcher = null;
    if (this.cache.containsKey(name)) {
      searcher = this.cache.get(name);
    } else {
      // synchronized (this) {
      // if (this.cache.containsKey(name)) {
      // searcher = this.cache.get(name);
      // } else {
      // IndexWriter writer = IndexWriterCache.getInstance().get(name);
      // IndexReader reader = null;
      // try {
      // reader = IndexReader.open(writer, true);
      // } catch (IOException ex) {
      // LOGGER.error("Something is wrong when open lucene IndexWriter", ex);
      // }
      // searcher = new IndexSearcher(reader);
      // this.cache.put(name, searcher);
      // }
      // }
    }
    return searcher;
  }

  public Map<String, IndexSearcher> getAll() {
    return this.cache;
  }

  public void put(String name, IndexSearcher searcher) {
    this.cache.put(name, searcher);
  }
}
