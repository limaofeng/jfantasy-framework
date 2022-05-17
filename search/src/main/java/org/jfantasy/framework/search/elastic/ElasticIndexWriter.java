package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.DocumentData;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Slf4j
public class ElasticIndexWriter implements IndexWriter {

  private static final int BATCH_COUNT = 100;
  private static final int DELAY_COMMIT = 3000;

  private final List<DocumentOperation> cachedDataList = new ArrayList(100);

  private final CuckooIndex cuckooIndex;
  private final ElasticsearchConnection connection;

  private Timer timer;

  public ElasticIndexWriter(CuckooIndex cuckooIndex, ElasticsearchConnection connection) {
    this.cuckooIndex = cuckooIndex;
    this.connection = connection;
  }

  @Override
  public void commit() throws IOException {
    List<DocumentOperation> data = new ArrayList<>(cachedDataList);
    cachedDataList.clear();
    timer = null;

    if (data.size() == 1) {
      DocumentOperation documentOperation = data.get(0);
      if (DocumentOperation.Action.create == documentOperation.getAction()) {
        this.create(documentOperation.getDoc());
      } else if (DocumentOperation.Action.update == documentOperation.getAction()) {
        this.update(documentOperation.getId(), documentOperation.getDoc());
      } else if (DocumentOperation.Action.delete == documentOperation.getAction()) {
        this.delete(documentOperation.getId());
      }
    } else {
      log.info("{}条数据，开始存储数据库！", data.size());
      if (data.size() == 1) {
        DocumentData doc = data.get(0);
        IndexRequest<Object> indexRequest =
            new IndexRequest.Builder<>()
                .index(doc.getIndexName())
                .id(doc.getId())
                .document(doc.getAttrs())
                .build();
        IndexResponse response = this.connection.getClient().index(indexRequest);
        log.debug("created response id: " + response.id());
      } else {
        List<BulkOperation> bulkOperations = new ArrayList<>();
        for (DocumentData doc : data) {
          bulkOperations.add(
              new BulkOperation.Builder()
                  .create(d -> d.index(doc.getIndexName()).id(doc.getId()).document(doc.getAttrs()))
                  .build());
        }

        BulkResponse response =
            this.connection
                .getClient()
                .bulk(new BulkRequest.Builder().operations(bulkOperations).build());
        log.debug(
            "batch created response ids: "
                + response.items().stream()
                    .map(BulkResponseItem::id)
                    .collect(Collectors.joining(",")));
        log.info("存储数据库成功！");
      }
    }
  }

  private void delete(String id) throws IOException {
    DeleteRequest request =
        new DeleteRequest.Builder()
            .index(this.cuckooIndex.getIndexName())
            .id(String.valueOf(id))
            .build();
    DeleteResponse response = connection.getClient().delete(request);
    log.debug("updated response id: " + response.id());
  }

  private void update(String id, DocumentData doc) throws IOException {
    UpdateRequest request =
        new UpdateRequest.Builder()
            .index(doc.getIndexName())
            .id(String.valueOf(id))
            .doc(doc.getAttrs())
            .build();
    UpdateResponse updateResponse = connection.getClient().update(request, Map.class);
    log.debug("updated response id: " + updateResponse.id());
  }

  private void create(DocumentData doc) throws IOException {
    IndexRequest<Object> indexRequest =
        new IndexRequest.Builder<>()
            .index(doc.getIndexName())
            .id(doc.getId())
            .document(doc.getAttrs())
            .build();
    IndexResponse response = this.connection.getClient().index(indexRequest);
    log.debug("created response id: " + response.id());
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
    cachedDataList.add(
        DocumentOperation.builder()
            .indexName(doc.getIndexName())
            .id(doc.getId())
            .action(DocumentOperation.Action.create)
            .build());
    this.delayCommit();
  }

  @Override
  public void updateDocument(Serializable id, DocumentData doc) throws IOException {
    this.cachedDataList.add(
        DocumentOperation.builder()
            .action(DocumentOperation.Action.update)
            .indexName(cuckooIndex.getIndexName())
            .id(String.valueOf(id))
            .doc(doc)
            .build());

    this.delayCommit();
  }

  @SneakyThrows
  private void delayCommit() {
    if (timer != null) {
      timer.cancel();
    }
    if (cachedDataList.size() >= 100) {
      this.commit();
    }
    // 延时 commit 逻辑
    timer = new Timer();
    timer.schedule(
        new TimerTask() {
          @SneakyThrows
          @Override
          public void run() {
            ElasticIndexWriter.this.commit();
          }
        },
        DELAY_COMMIT);
  }
}
