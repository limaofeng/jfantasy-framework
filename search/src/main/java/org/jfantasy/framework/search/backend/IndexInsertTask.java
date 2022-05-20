package org.jfantasy.framework.search.backend;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.Document;
import org.jfantasy.framework.search.cache.IndexCache;
import org.jfantasy.framework.search.elastic.IndexWriter;

@Slf4j
public class IndexInsertTask implements Runnable {
  private final Object entity;

  public IndexInsertTask(Object entity) {
    this.entity = entity;
  }

  @Override
  public void run() {
    Class<?> clazz = this.entity.getClass();
    CuckooIndex cuckooIndex = IndexCache.getInstance().get(clazz);
    IndexWriter writer = cuckooIndex.getIndexWriter();

    Document doc = new Document(cuckooIndex.getIndexName());
    IndexCreator creator = new IndexCreator(this.entity, "");
    creator.create(doc);
    try {
      writer.addDocument(doc);
    } catch (IOException ex) {
      log.error("IndexWriter can not add a document to the lucene index", ex);
    }
  }
}
