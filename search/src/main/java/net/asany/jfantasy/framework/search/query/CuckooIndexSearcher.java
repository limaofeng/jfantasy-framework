package net.asany.jfantasy.framework.search.query;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.search.CuckooIndex;
import net.asany.jfantasy.framework.search.Highlighter;
import net.asany.jfantasy.framework.search.cache.DaoCache;
import net.asany.jfantasy.framework.search.cache.IndexCache;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.search.dao.CuckooDao;
import net.asany.jfantasy.framework.search.elastic.SmartSearcher;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.error.UnsupportedException;
import net.asany.jfantasy.framework.util.reflect.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
public class CuckooIndexSearcher<T> {

  private final Class<T> entityClass;
  private final LoadEntityMode loadMode;

  private CuckooDao cuckooDao;

  protected CuckooIndexSearcher() {
    this(LoadEntityMode.es);
  }

  protected CuckooIndexSearcher(LoadEntityMode loadMode) {
    this.loadMode = loadMode;
    // 通过泛型获取需要查询的对象
    this.entityClass =
        (Class<T>) ClassUtil.getSuperClassGenricType(ClassUtil.getRealClass(getClass()));
  }

  protected CuckooDao dataFetcher() {
    if (this.cuckooDao == null) {
      this.cuckooDao = DaoCache.getInstance().get(this.entityClass);
    }
    return this.cuckooDao;
  }

  protected CuckooIndex cuckooIndex() {
    return IndexCache.getInstance().get(this.entityClass);
  }

  protected SmartSearcher<T> searcher(Query query) {
    return cuckooIndex().searcher(query);
  }

  /**
   * 返回查询的结果
   *
   * @param query 查询条件
   * @param size 返回条数
   * @return List<T>
   */
  @SneakyThrows(IOException.class)
  public List<T> search(Query query, int size) {
    SearchResponse<T> response = searcher(query).withSize(size).search();
    return buildResults(response);
  }

  /**
   * 返回查询的结果 支持排序
   *
   * @param query 查询条件
   * @param size 返回条数
   * @param sort 排序设置
   * @return List<T>
   */
  @SneakyThrows(IOException.class)
  public List<T> search(Query query, int size, Sort sort) {
    SearchResponse<T> response = searcher(query).withSize(size).withSort(sort).search();
    return buildResults(response);
  }

  /**
   * 返回查询的结果 (支持关键字高亮)
   *
   * @param query 查询条件
   * @param size 返回条数
   * @param sort 排序设置
   * @param highlighter 关键字高亮
   * @return List<T>
   */
  @SneakyThrows(IOException.class)
  public List<T> search(Query query, int size, Sort sort, Highlighter highlighter) {
    SearchResponse<T> response =
        searcher(query).withSize(size).withSort(sort).withHighlight(highlighter).search();
    return buildResults(response);
  }

  /**
   * 返回查询的结果 (支持关键字高亮)
   *
   * @param query 查询条件
   * @param size 返回条数
   * @param highlighter 关键字高亮
   * @return List<T>
   */
  @SneakyThrows(IOException.class)
  public List<T> search(Query query, int size, Highlighter highlighter) {
    SearchResponse<T> response = searcher(query).withSize(size).withHighlight(highlighter).search();
    return buildResults(response);
  }

  /**
   * 分页查询
   *
   * @param query 查询条件
   * @param pageable 翻页对象
   * @return Pager<T>
   */
  @SneakyThrows(IOException.class)
  public Page<T> search(Query query, Pageable pageable) {
    SearchResponse<T> response =
        searcher(query)
            .withOffset((int) pageable.getOffset())
            .withSize(pageable.getPageSize())
            .withSort(pageable.getSort())
            .search();

    HitsMetadata<T> hitsMetadata = response.hits();

    return PageableExecutionUtils.getPage(
        buildResults(response),
        pageable,
        () -> {
          assert hitsMetadata.total() != null;
          return hitsMetadata.total().value();
        });
  }

  /**
   * 支持翻页及高亮查询
   *
   * @param query 查询条件
   * @param pageable 翻页对象
   * @param highlighter 关键字高亮
   * @return Pager<T>
   */
  @SneakyThrows(IOException.class)
  public Page<T> search(Query query, Pageable pageable, Highlighter highlighter) {
    SearchResponse<T> response =
        searcher(query)
            .withOffset((int) pageable.getOffset())
            .withSize(pageable.getPageSize())
            .withSort(pageable.getSort())
            .withHighlight(highlighter)
            .search();

    HitsMetadata<T> hitsMetadata = response.hits();

    return PageableExecutionUtils.getPage(
        buildResults(response),
        pageable,
        () -> {
          assert hitsMetadata.total() != null;
          return hitsMetadata.total().value();
        });
  }

  protected List<T> buildResults(SearchResponse<T> response) {
    List<T> data = new ArrayList<>();
    HitsMetadata<T> hitsMetadata = response.hits();
    for (Hit<T> hit : hitsMetadata.hits()) {
      T pd = hit.source();
      data.add(this.buildEntity(pd, hit, response));
    }
    return data;
  }

  protected T buildEntity(T pd, Hit<T> hit, SearchResponse<T> search) {
    if (LoadEntityMode.dao == this.loadMode) {
      Serializable id = PropertysCache.getInstance().getIdProperty(entityClass).getValue(pd);
      pd = dataFetcher().getById(id);
    }
    highlight(pd, hit.highlight());
    return pd;
  }

  protected void highlight(Object obj, Map<String, List<String>> fields) {
    for (Map.Entry<String, List<String>> entry : fields.entrySet()) {
      String fieldName = entry.getKey();
      List<String> values = entry.getValue();
      if (!fieldName.contains(".")) {
        Property property =
            PropertysCache.getInstance().getPropertyByFieldName(this.entityClass, fieldName);
        assert property != null;
        property.setValue(obj, values.get(0));
      } else {
        throw new UnsupportedException("子对象字段高亮转换还未支持, 可以通过覆盖该函数，进行自定义实现");
      }
    }
  }

  public enum LoadEntityMode {
    es,
    dao
  }
}
