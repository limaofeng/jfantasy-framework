package org.jfantasy.framework.search;

import java.io.IOException;
import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.backend.EntityChangedListener;
import org.jfantasy.framework.search.elastic.IndexSearcher;
import org.jfantasy.framework.search.elastic.IndexWriter;

public interface CuckooIndex {

  Class getDocumentClass();

  Document getDocument();

  IndexWriter getIndexWriter();

  <T> IndexSearcher<T> getIndexSearcher();

  EntityChangedListener getEntityChangedListener();

    String getIndexName();
}
