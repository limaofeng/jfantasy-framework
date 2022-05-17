package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.query.Query;

public class ElasticIndexSearcher implements IndexSearcher<Object> {

  private final CuckooIndex cuckooIndex;
  private final ElasticsearchConnection connection;

  public ElasticIndexSearcher(CuckooIndex cuckooIndex, ElasticsearchConnection connection) {
    this.cuckooIndex = cuckooIndex;
    this.connection = connection;
  }

  @Override
  public SearchResponse<Object> search(Query query, int size) throws IOException {
    SearchRequest request =
        new SearchRequest.Builder()
            .index(cuckooIndex.getDocument().indexName())
            .query(query.toQuery()._toQuery())
            .build();
    ElasticsearchClient client = connection.getClient();
    return client.search(request, cuckooIndex.getDocumentClass());
  }
}
