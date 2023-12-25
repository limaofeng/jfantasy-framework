package net.asany.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import net.asany.jfantasy.framework.search.CuckooIndex;
import net.asany.jfantasy.framework.search.Highlighter;
import net.asany.jfantasy.framework.search.query.Query;
import org.springframework.data.domain.Sort;

public class SmartSearcherImpl<T> implements SmartSearcher<T> {

  private final CuckooIndex cuckooIndex;
  private final ElasticsearchConnection connection;
  private final SearchRequest.Builder requestBuilder;

  public SmartSearcherImpl(
      CuckooIndex cuckooIndex, ElasticsearchConnection connection, Query query) {
    this.cuckooIndex = cuckooIndex;
    this.connection = connection;
    this.requestBuilder =
        new SearchRequest.Builder()
            .index(cuckooIndex.getIndexName())
            .query(query.toQuery()._toQuery());
  }

  @Override
  public SmartSearcher<T> withSize(int size) {
    this.requestBuilder.size(size);
    return this;
  }

  @Override
  public SmartSearcher<T> withOffset(int offset) {
    this.requestBuilder.from(offset);
    return this;
  }

  @Override
  public SmartSearcher<T> withSort(Sort sort) {
    if (sort == null || sort.isUnsorted()) {
      return this;
    }
    List<SortOptions> sortOptions =
        sort.stream()
            .map(
                order -> {
                  SortOrder sortOrder =
                      order.getDirection().isAscending() ? SortOrder.Asc : SortOrder.Desc;

                  FieldSort fieldSort =
                      new FieldSort.Builder().field(order.getProperty()).order(sortOrder).build();
                  return new SortOptions.Builder().field(fieldSort).build();
                })
            .collect(Collectors.toList());
    requestBuilder.sort(sortOptions);
    return this;
  }

  @Override
  public SmartSearcher<T> withHighlight(Highlighter highlighter) {
    if (highlighter == null) {
      return this;
    }
    this.requestBuilder.highlight(highlighter.build());
    return this;
  }

  @Override
  public SearchResponse<T> search() throws IOException {
    ElasticsearchClient client = connection.getClient();
    return client.search(this.requestBuilder.build(), cuckooIndex.getIndexClass());
  }
}
