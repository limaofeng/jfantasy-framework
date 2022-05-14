package org.jfantasy.framework.search.backend;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.mapper.MapperUtil;

@Slf4j
public class IndexInsertTask implements Runnable {
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
