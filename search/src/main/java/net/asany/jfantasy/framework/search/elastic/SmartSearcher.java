package net.asany.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import net.asany.jfantasy.framework.search.Highlighter;
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

  /**
   * 设置返回结果偏移量
   *
   * @param offset int
   * @return SmartSearcher<T>
   */
  SmartSearcher<T> withOffset(int offset);

  /**
   * 设置排序
   *
   * @param sort Sort
   * @return SmartSearcher<T>
   */
  SmartSearcher<T> withSort(Sort sort);

  /**
   * 设置关键字高亮
   *
   * @param highlighter Highlighter
   * @return SmartSearcher<T>
   */
  SmartSearcher<T> withHighlight(Highlighter highlighter);

  /**
   * 执行搜索
   *
   * @return SearchResponse<T>
   * @throws IOException 异常
   */
  SearchResponse<T> search() throws IOException;
}
