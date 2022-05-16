package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.jfantasy.framework.search.Query;

import java.io.IOException;

public interface IndexSearcher<T> {
    SearchResponse<T> search(Query query, int size) throws IOException;
}
