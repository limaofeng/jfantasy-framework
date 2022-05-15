package org.jfantasy.framework.search;

import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.elastic.IndexWriter;

import javax.print.Doc;
import java.io.IOException;

public interface CuckooIndex {

  void createIndex() throws IOException;

  Document getDocument();

  IndexWriter getIndexWriter();

}
