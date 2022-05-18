package org.jfantasy.framework.search.elastic;

import java.io.IOException;
import java.io.Serializable;
import org.jfantasy.framework.search.DocumentData;

public interface IndexWriter {

  void commit() throws IOException;

  void deleteAll() throws IOException;

  void addDocument(DocumentData doc) throws IOException;

  void updateDocument(Serializable id, DocumentData doc) throws IOException;

  void deleteDocument(Serializable id) throws IOException;
}
