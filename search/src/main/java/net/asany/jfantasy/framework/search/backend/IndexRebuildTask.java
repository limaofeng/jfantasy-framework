package net.asany.jfantasy.framework.search.backend;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.search.CuckooIndex;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.cache.DaoCache;
import net.asany.jfantasy.framework.search.cache.IndexCache;
import net.asany.jfantasy.framework.search.dao.CuckooDao;
import net.asany.jfantasy.framework.search.elastic.IndexWriter;
import net.asany.jfantasy.framework.util.common.ClassUtil;

@Slf4j
public class IndexRebuildTask implements Runnable {
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
      if (log.isErrorEnabled()) {
        log.error("Another rebuilding task is running");
      }
      return;
    }
    try {
      if (log.isInfoEnabled()) {
        log.info("Index(" + this.clazz + ") rebuilding start...");
      }
      try {
        this.writer.deleteAll();
      } catch (IOException ex) {
        if (log.isErrorEnabled()) {
          log.error("Something is wrong when lucene IndexWriter doing deleteAll()", ex);
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
        log.error("Can not commit and close the index", ex);
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
        log.error("IndexWriter can not add a document to the lucene index", ex);
      }
    }
  }
}
