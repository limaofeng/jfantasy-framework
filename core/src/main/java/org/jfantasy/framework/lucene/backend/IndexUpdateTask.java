package org.jfantasy.framework.lucene.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfantasy.framework.lucene.cache.IndexWriterCache;
import org.jfantasy.framework.lucene.mapper.MapperUtil;

public class IndexUpdateTask implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(IndexUpdateTask.class);
  private Object entity;

  public IndexUpdateTask(Object entity) {
    this.entity = entity;
  }

  @Override
  public void run() {
    Class<?> clazz = this.entity.getClass();
    String name = MapperUtil.getEntityName(clazz);
    IndexWriterCache cache = IndexWriterCache.getInstance();
    // IndexWriter writer = cache.get(name);
    // Document doc = new Document();
    // IndexCreator creator = new IndexCreator(this.entity, "");
    // creator.create(doc);
    // Property property = PropertysCache.getInstance().getIdProperty(clazz);
    // Term term = new
    // Term(property.getName(),property.getValue(this.entity).toString());
    // try {
    // writer.updateDocument(term, doc);
    // } catch (IOException ex) {
    // LOGGER.error("IndexWriter can not update the document", ex);
    // }
  }
}
