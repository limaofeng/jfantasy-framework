package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.Document;

@Slf4j
public class ElasticIndexWriter implements IndexWriter {

  private static final int DELAY_COMMIT = 3000;

  private List<Task> cachedDataList;
  private final int batchSize;
  private final CuckooIndex cuckooIndex;
  private final ElasticsearchConnection connection;

  private Timer timer;

  public ElasticIndexWriter(
      CuckooIndex cuckooIndex, ElasticsearchConnection connection, int batchSize) {
    this.cuckooIndex = cuckooIndex;
    this.connection = connection;
    this.batchSize = batchSize;
    this.cachedDataList = new ArrayList(batchSize);
  }

  @Override
  public synchronized void commit() throws IOException {
    List<Task> data = new ArrayList<>(cachedDataList);
    cachedDataList = new ArrayList<>(batchSize);

    // 如果存在定时器，就取消它
    if (timer != null) {
      timer.cancel();
    }
    timer = null;

    // 执行处理逻辑
    log.info("{}条数据，开始存储到Elasticsearch！", data.size());
    try {
      if (data.size() == 1) {
        // 如果只有一条数据
        Task task = data.get(0);
        if (Operation.create == task.getOperation()) {
          this.create(task.getDoc());
        } else if (Operation.update == task.getOperation()) {
          this.update(task.getId(), task.getDoc());
        } else if (Operation.delete == task.getOperation()) {
          this.delete(task.getId());
        }
      } else {
        // 批量处理
        List<BulkOperation> bulkOperations = new ArrayList<>();
        for (Task task : data) {
          Document doc = task.getDoc();
          if (Operation.create == task.getOperation()) {
            bulkOperations.add(
                new BulkOperation.Builder()
                    .create(
                        d -> d.index(doc.getIndexName()).id(doc.getId()).document(doc.getAttrs()))
                    .build());
          } else if (Operation.update == task.getOperation()) {
            bulkOperations.add(
                new BulkOperation.Builder()
                    .update(
                        builder ->
                            builder
                                .index(doc.getIndexName())
                                .id(doc.getId())
                                .action(builder1 -> builder1.doc(doc.getAttrs())))
                    .build());
          } else if (Operation.delete == task.getOperation()) {
            bulkOperations.add(
                new BulkOperation.Builder()
                    .delete(d -> d.index(task.getIndexName()).id(task.getId()))
                    .build());
          }
        }
        BulkResponse response =
            this.connection
                .getClient()
                .bulk(
                    new BulkRequest.Builder()
                        .index(this.cuckooIndex.getIndexName())
                        .operations(bulkOperations)
                        .build());
        log.debug(
            "batch created response ids: "
                + response.items().stream()
                    .map(BulkResponseItem::id)
                    .collect(Collectors.joining(",")));
      }
      log.info("存储Elasticsearch成功！");
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  private void delete(String id) throws IOException {
    DeleteRequest request =
        new DeleteRequest.Builder()
            .index(this.cuckooIndex.getIndexName())
            .id(String.valueOf(id))
            .build();
    DeleteResponse response = connection.getClient().delete(request);
    log.debug("deleted response id: " + response.id());
  }

  private void update(String id, Document doc) throws IOException {
    UpdateRequest request =
        new UpdateRequest.Builder()
            .index(doc.getIndexName())
            .id(String.valueOf(id))
            .doc(doc.getAttrs())
            .build();
    UpdateResponse updateResponse = connection.getClient().update(request, Map.class);
    log.debug("updated response id: " + updateResponse.id());
  }

  private void create(Document doc) throws IOException {
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
                    .index(cuckooIndex.getIndexName())
                    .query(builder1 -> builder1.matchAll(builder2 -> builder2)));
  }

  @Override
  public void addDocument(Document doc) throws IOException {
    cachedDataList.add(
        Task.builder()
            .indexName(doc.getIndexName())
            .id(doc.getId())
            .operation(Operation.create)
            .doc(doc)
            .build());
    this.delayCommit();
  }

  @Override
  public void updateDocument(Serializable id, Document doc) throws IOException {
    this.cachedDataList.add(
        Task.builder()
            .operation(Operation.update)
            .indexName(cuckooIndex.getIndexName())
            .id(String.valueOf(id))
            .doc(doc)
            .build());

    this.delayCommit();
  }

  @Override
  public void deleteDocument(Serializable id) throws IOException {
    this.cachedDataList.add(
        Task.builder()
            .operation(Operation.delete)
            .indexName(cuckooIndex.getIndexName())
            .id(String.valueOf(id))
            .build());

    this.delayCommit();
  }

  private void delayCommit() throws IOException {
    if (cachedDataList.size() >= 100) {
      this.commit();
      return;
    }

    // 取消之前的定时器
    if (timer != null) {
      timer.cancel();
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

  private enum Operation {
    create,
    update,
    delete
  }

  @Data
  @Builder
  @AllArgsConstructor
  private static class Task {
    private String indexName;
    private String id;
    private Operation operation;
    private Document doc;
  }
}
