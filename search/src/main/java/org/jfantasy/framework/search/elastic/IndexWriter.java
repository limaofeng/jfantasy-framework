package org.jfantasy.framework.search.elastic;

import java.io.IOException;
import org.jfantasy.framework.search.DocumentData;

public interface IndexWriter {

  void commit() throws IOException;

  void deleteAll() throws IOException;

  void addDocument(DocumentData doc) throws IOException;
}
