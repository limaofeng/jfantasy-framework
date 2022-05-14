package org.jfantasy.framework.search.elastic;

import java.io.IOException;
import org.jfantasy.framework.search.Document;

public class ElasticIndexWriter implements IndexWriter {

  @Override
  public void commit() throws IOException {}

  @Override
  public void deleteAll() throws IOException {}

  @Override
  public void addDocument(Document doc) throws IOException {}
}
