package org.jfantasy.framework.search;

import org.jfantasy.framework.search.elastic.IndexWriter;

public interface IndexedFactory {

  IndexWriter createIndexWriter(Class entity);
}
