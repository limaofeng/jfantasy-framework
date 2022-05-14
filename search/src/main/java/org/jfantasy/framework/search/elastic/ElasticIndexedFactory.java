package org.jfantasy.framework.search.elastic;

import org.jfantasy.framework.search.IndexedFactory;

public class ElasticIndexedFactory implements IndexedFactory {
  @Override
  public IndexWriter createIndexWriter(Class entity) {
    return new ElasticIndexWriter();
  }
}
