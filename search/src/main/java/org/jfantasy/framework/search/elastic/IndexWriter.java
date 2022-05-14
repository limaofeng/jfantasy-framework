package org.jfantasy.framework.search.elastic;

import java.io.IOException;
import org.jfantasy.framework.search.Document;

public interface IndexWriter {

  void commit() throws IOException;

  void deleteAll() throws IOException;

  void addDocument(Document doc) throws IOException;
}
