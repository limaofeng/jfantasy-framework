package org.jfantasy.framework.search.backend;

import java.io.IOException;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.Document;
import org.jfantasy.framework.search.cache.IndexCache;
import org.jfantasy.framework.search.cache.PropertysCache;
import org.jfantasy.framework.search.elastic.IndexWriter;
import org.jfantasy.framework.util.reflect.Property;

@Slf4j
public class IndexUpdateTask implements Runnable {
  private final Object entity;

  public IndexUpdateTask(Object entity) {
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

    Property property = PropertysCache.getInstance().getIdProperty(clazz);
    Serializable id = property.getValue(this.entity);
    try {
      writer.updateDocument(id, doc);
    } catch (IOException ex) {
      log.error("IndexWriter can not update the document", ex);
    }
  }
}
