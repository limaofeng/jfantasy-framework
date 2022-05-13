package org.jfantasy.framework.search.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfantasy.framework.search.mapper.MapperUtil;

public class IndexInsertTask implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(IndexInsertTask.class);
  private Object entity;

  public IndexInsertTask(Object entity) {
    this.entity = entity;
  }

  @Override
  public void run() {
    Class<?> clazz = this.entity.getClass();
    String name = MapperUtil.getEntityName(clazz);
    // IndexWriterCache cache = IndexWriterCache.getInstance();
    // IndexWriter writer = cache.get(name);
    // Document doc = new Document();
    // IndexCreator creator = new IndexCreator(this.entity, "");
    // creator.create(doc);
    // try {
    // writer.addDocument(doc);
    // } catch (IOException ex) {
    // LOGGER.error("IndexWriter can not add a document to the lucene index", ex);
    // }
  }
}
