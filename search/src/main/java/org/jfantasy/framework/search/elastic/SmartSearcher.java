package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import org.jfantasy.framework.search.Highlighter;
import org.springframework.data.domain.Sort;

public interface SmartSearcher<T> {
  SmartSearcher<T> withSize(int size);

  SmartSearcher<T> withOffset(int offset);

  SmartSearcher<T> withSort(Sort sort);

  SmartSearcher<T> withHighlight(Highlighter highlighter);

  SearchResponse<T> search() throws IOException;
}
