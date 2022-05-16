package org.jfantasy.framework.search;

import java.io.IOException;
import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.elastic.IndexWriter;

public interface CuckooIndex {

  void createIndex() throws IOException;

  Document getDocument();

  IndexWriter getIndexWriter();
}
