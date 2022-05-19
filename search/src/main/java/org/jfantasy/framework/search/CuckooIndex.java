package org.jfantasy.framework.search;

import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.elastic.IndexWriter;
import org.jfantasy.framework.search.elastic.SmartSearcher;
import org.jfantasy.framework.search.query.Query;

public interface CuckooIndex {

  Class getDocumentClass();

  Document getDocument();

  IndexWriter getIndexWriter();

  String getIndexName();

  <T> SmartSearcher<T> searcher(Query query);
}
