package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.Query;

import java.io.IOException;

public class ElasticIndexSearcher implements IndexSearcher<Object> {

  private final CuckooIndex cuckooIndex;
  private final ElasticsearchConnection connection;

  public ElasticIndexSearcher(CuckooIndex cuckooIndex, ElasticsearchConnection connection) {
    this.cuckooIndex = cuckooIndex;
    this.connection = connection;
  }

  @Override
  public SearchResponse<Object> search(Query _query, int size) throws IOException {
    TermQuery query = QueryBuilders.term().field("id").value("1").build();
    SearchRequest request =
        new SearchRequest.Builder()
            .index(cuckooIndex.getDocument().indexName())
            .query(query._toQuery())
            .build();
    ElasticsearchClient client = connection.getClient();
    return client.search(request, cuckooIndex.getDocumentClass());
  }
}
