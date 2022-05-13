package org.jfantasy.framework.search.backend;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfantasy.framework.search.elastic.IndexReader;
import org.jfantasy.framework.search.elastic.IndexSearcher;

public class IndexReopenTask implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(IndexReopenTask.class);
  private static final Lock reopenLock = new ReentrantLock();

  @Override
  public void run() {
    if (!reopenLock.tryLock()) {
      return;
    }
    try {
      // IndexSearcherCache searcherCache = IndexSearcherCache.getInstance();
      // for (Map.Entry<String, IndexSearcher> entry :
      // searcherCache.getAll().entrySet())
      // {
      // IndexSearcher searcher = entry.getValue();
      // IndexSearcher newSearcher = reopen(searcher);
      // if (newSearcher != null) {
      // searcherCache.put(entry.getKey(), newSearcher);
      // }
      // }
    } finally {
      reopenLock.unlock();
    }
  }

  private IndexSearcher reopen(IndexSearcher searcher) {
    // IndexReader reader = searcher.getIndexReader();
    // try {
    // IndexReader newReader = IndexReader.openIfChanged(reader);//NOSONAR
    // if (newReader != null && newReader != reader) {//NOSONAR
    // close(reader);
    // return new IndexSearcher(newReader);
    // }
    // } catch (IOException ex) {
    // LOGGER.error("Something is wrong when reopen the Lucene IndexReader", ex);
    // return null;
    // }
    return null;
  }

  private void close(IndexReader reader) {
    try {
      reader.decRef();
    } catch (IOException ex) {
      LOGGER.error("Something is wrong when decrease the reference of the lucene IndexReader", ex);
    }
  }
}
