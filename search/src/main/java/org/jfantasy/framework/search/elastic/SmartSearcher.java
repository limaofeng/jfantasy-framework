package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import org.jfantasy.framework.search.Highlighter;
import org.springframework.data.domain.Sort;

/**
 * 智能搜索器
 *
 * @param <T> 搜索结果类型
 * @author limaofeng
 */
public interface SmartSearcher<T> {
  /**
   * 设置返回结果数量
   *
   * @param size int
   * @return SmartSearcher<T>
   */
  SmartSearcher<T> withSize(int size);

  SmartSearcher<T> withOffset(int offset);

  SmartSearcher<T> withSort(Sort sort);

  SmartSearcher<T> withHighlight(Highlighter highlighter);

  SearchResponse<T> search() throws IOException;
}
