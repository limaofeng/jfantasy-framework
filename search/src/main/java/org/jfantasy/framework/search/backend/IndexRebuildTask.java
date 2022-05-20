package org.jfantasy.framework.search.backend;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.Document;
import org.jfantasy.framework.search.cache.DaoCache;
import org.jfantasy.framework.search.cache.IndexCache;
import org.jfantasy.framework.search.dao.CuckooDao;
import org.jfantasy.framework.search.elastic.IndexWriter;
import org.jfantasy.framework.util.common.ClassUtil;

public class IndexRebuildTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(IndexRebuildTask.class);
  private static final ConcurrentMap<Class<?>, ReentrantLock> rebuildLocks =
      new ConcurrentHashMap<>();
  private final Lock rebuildLock;
  private final Class<?> clazz;
  private final IndexWriter writer;
  private final int batchSize;

  public IndexRebuildTask(Class<?> clazz, int batchSize) {
    this.clazz = clazz;
    this.batchSize = batchSize;
    IndexCache cache = IndexCache.getInstance();
    this.writer = cache.get(clazz).getIndexWriter();
    if (!rebuildLocks.containsKey(clazz)) {
      rebuildLocks.put(clazz, new ReentrantLock());
    }
    rebuildLock = rebuildLocks.get(clazz);
  }

  @Override
  public void run() {
    if (!rebuildLock.tryLock()) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Another rebuilding task is running");
      }
      return;
    }
    try {
      if (LOG.isInfoEnabled()) {
        LOG.info("Index(" + this.clazz + ") rebuilding start...");
      }
      try {
        this.writer.deleteAll();
      } catch (IOException ex) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Something is wrong when lucene IndexWriter doing deleteAll()", ex);
        }
      }
      final CuckooDao luceneDao = DaoCache.getInstance().get(clazz);
      long count = luceneDao.count();
      int pages = (int) (count / this.batchSize);
      int remainder = (int) (count % this.batchSize);
      if (pages > 0) {
        for (int i = 1; i <= pages; i++) {
          List<?> list = luceneDao.find((i - 1) * this.batchSize, this.batchSize);
          process(list);
        }
      }
      if (remainder > 0) {
        pages++;
        List<?> list = luceneDao.find((pages - 1) * this.batchSize, remainder);
        process(list);
      }
      try {
        this.writer.commit();
      } catch (IOException ex) {
        LOG.error("Can not commit and close the index", ex);
      }
    } finally {
      rebuildLock.unlock();
    }
  }

  private void process(List<?> list) {
    for (Object o : list) {
      process(o);
    }
  }

  private void process(Object entity) {
    IndexFilterChecker checker = new IndexFilterChecker(entity);
    if (checker.needIndex()) {
      CuckooIndex cuckooIndex =
          IndexCache.getInstance().get(ClassUtil.getRealClass(entity.getClass()));
      Document doc = new Document(cuckooIndex.getIndexName());
      IndexCreator creator = new IndexCreator(entity, "");
      creator.create(doc);
      try {
        this.writer.addDocument(doc);
      } catch (IOException ex) {
        LOG.error("IndexWriter can not add a document to the lucene index", ex);
      }
    }
  }

  //  public abstract class ProcessCallback implements JdbcUtil.Callback<Void> {
  //    protected int start;
  //    protected int size;
  //
  //    ProcessCallback(int start, int size) {
  //      this.start = start;
  //      this.size = size;
  //    }
  //
  //    @Override
  //    public abstract Void run();
  //  }
}
