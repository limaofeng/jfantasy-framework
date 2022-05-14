package org.jfantasy.framework.search.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfantasy.framework.search.Document;

public class ElasticIndexWriter implements IndexWriter {

  private final List cachedDataList = new ArrayList(100);

  @Override
  public void commit() throws IOException {
    System.out.println("commit:\t" + cachedDataList.toString());
    cachedDataList.clear();
  }

  @Override
  public void deleteAll() throws IOException {
    System.out.println("deleteAll");
  }

  @Override
  public void addDocument(Document doc) throws IOException {
    System.out.println("commit");
    cachedDataList.add(doc);
  }
}
