package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import org.jfantasy.framework.search.query.Query;

public interface IndexSearcher<T> {
  SearchResponse<T> search(Query query, int size) throws IOException;
}
