package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.DocumentData;

public class ElasticIndexWriter implements IndexWriter {

  private final List<DocumentData> cachedDataList = new ArrayList(100);

  private final CuckooIndex cuckooIndex;
  private final ElasticsearchConnection connection;

  public ElasticIndexWriter(CuckooIndex cuckooIndex, ElasticsearchConnection connection) {
    this.cuckooIndex = cuckooIndex;
    this.connection = connection;
  }

  @Override
  public void commit() throws IOException {
    List<DocumentData> data = new ArrayList<>(cachedDataList);
    cachedDataList.clear();

    if (data.size() == 1) {
      DocumentData doc = data.get(0);
      IndexRequest<Object> indexRequest =
          new IndexRequest.Builder<>()
              .index(doc.getIndexName())
              .id(doc.getId())
              .document(doc.getAttrs())
              .build();
      IndexResponse response = this.connection.getClient().index(indexRequest);
    } else {
      List<BulkOperation> bulkOperations = new ArrayList<>();
      for (DocumentData doc : data) {
        bulkOperations.add(
            new BulkOperation.Builder()
                .create(d -> d.index(doc.getIndexName()).id(doc.getId()).document(doc.getAttrs()))
                .build());
      }
      this.connection
          .getClient()
          .bulk(new BulkRequest.Builder().operations(bulkOperations).build());
    }
  }

  @Override
  public void deleteAll() throws IOException {
    connection
        .getClient()
        .deleteByQuery(
            builder ->
                builder
                    .index(cuckooIndex.getDocument().indexName())
                    .query(builder1 -> builder1.matchAll(builder2 -> builder2)));
  }

  @Override
  public void addDocument(DocumentData doc) throws IOException {
    cachedDataList.add(doc);
  }
}
