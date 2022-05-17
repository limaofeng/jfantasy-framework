package org.jfantasy.framework.search.query;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.cache.DaoCache;
import org.jfantasy.framework.search.cache.IndexCache;
import org.jfantasy.framework.search.cache.PropertysCache;
import org.jfantasy.framework.search.dao.DataFetcher;
import org.jfantasy.framework.search.elastic.IndexSearcher;
import org.jfantasy.framework.search.exception.IdException;
import org.jfantasy.framework.search.query.Query;
import org.jfantasy.framework.util.common.ClassUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CuckooIndexSearcher<T> {

  private Class<T> entityClass;
  private String idName;
  private final LoadEntityMode loadMode;

  private DataFetcher dataFetcher;

  protected CuckooIndexSearcher() {
    this(LoadEntityMode.es);
  }

  protected CuckooIndexSearcher(LoadEntityMode loadMode) {
    this.loadMode = loadMode;
    // 通过泛型获取需要查询的对象
    this.entityClass =
        (Class<T>) ClassUtil.getSuperClassGenricType(ClassUtil.getRealClass(getClass()));
    try {
      // 获取对象的主键idName
      idName = PropertysCache.getInstance().getIdProperty(this.entityClass).getName();
    } catch (IdException e) {
      log.error(e.getMessage(), e);
    }
  }

  protected DataFetcher dataFetcher() {
    if (this.dataFetcher == null) {
      this.dataFetcher = DaoCache.getInstance().get(this.entityClass);
    }
    return this.dataFetcher;
  }

  protected CuckooIndex cuckooIndex() {
    return IndexCache.getInstance().get(this.entityClass);
  }

  private IndexSearcher<T> searcher() {
    return cuckooIndex().getIndexSearcher();
  }

  /**
   * 返回查询的结果
   *
   * @param query 查询条件
   * @param size 返回条数
   * @return List<T>
   */
  public List<T> search(Query query, int size) throws IOException {
    List<T> data = new ArrayList<>();
    SearchResponse<T> response = searcher().search(query, size);
    HitsMetadata<T> hitsMetadata = response.hits();
    for (Hit<T> hit : hitsMetadata.hits()) {
      T pd = hit.source();
      data.add(this.build(pd, hit, response));
    }
    return data;
  }

  /**
   * 支持翻页及高亮查询
   *
   * @param pager 翻页对象
   * @param query 查询条件
   * @param highlighter 关键字高亮
   * @return Pager<T>
   */
  //  public Pager<T> search(Pager<T> pager, Query query, BuguHighlighter highlighter) {
  //    IndexSearcher searcher = open();
  //    int between = 0;
  //    try {
  //      TopDocs hits;
  //      if (pager.isOrderBySetted()) { // TODO 多重排序等HIbernateDao优化好之后再实现
  //        hits =
  //            searcher.search(
  //                query,
  //                pager.getCurrentPage() * pager.getPageSize(),
  //                new Sort(
  //                    new SortField(
  //                        pager.getOrderBy(),
  //                        getSortField(pager.getOrderBy()),
  //                        Pager.Order.asc == pager.getOrders()[0])));
  //      } else {
  //        hits = searcher.search(query, pager.getCurrentPage() * pager.getPageSize());
  //        int index = (pager.getCurrentPage() - 1) * pager.getPageSize();
  //        if (hits.totalHits > 0 && hits.scoreDocs.length > 0) {
  //          ScoreDoc scoreDoc = index > 0 ? hits.scoreDocs[index - 1] : null;
  //          hits = searcher.searchAfter(scoreDoc, query, pager.getPageSize());
  //        }
  //        between = index;
  //      }
  //      pager.setTotalCount(hits.totalHits);
  //      List<T> data = new ArrayList<T>();
  //      for (int i = pager.getFirst() - between;
  //          i < hits.scoreDocs.length && hits.totalHits > 0;
  //          i++) {
  //        ScoreDoc sdoc = hits.scoreDocs[i];
  //        Document doc = searcher.doc(sdoc.doc);
  //        data.add(this.build(doc));
  //      }
  //      pager.setPageItems(data);
  //      if (highlighter != null) {
  //        for (T obj : pager.getPageItems()) {
  //          highlightObject(highlighter, obj);
  //        }
  //      }
  //    } catch (IOException e) {
  //      LOGGER.error(e.getMessage(), e);
  //    } finally {
  //      close(searcher);
  //    }
  //    return pager;
  //  }

  /**
   * @param pager 翻页对象
   * @param query 查询条件
   * @param fields 需要高亮显示的字段
   * @param keyword 高亮关键字
   * @return Pager<T>
   */
  //  public Pager<T> search(Pager<T> pager, Query query, String[] fields, String keyword) {
  //    if (StringUtil.isNotBlank(keyword)) {
  //      return this.search(pager, query, new BuguHighlighter(fields, keyword));
  //    } else {
  //      return this.search(pager, query);
  //    }
  //  }

  /**
   * @param pager 翻页对象
   * @param query 查询条件
   * @return Pager<T>
   */
  //  public Pager<T> search(Pager<T> pager, Query query) {
  //    return this.search(pager, query, null);
  //  }

  //  /**
  //   * 将javaType 转换为 SortField
  //   *
  //   * @param fieldName 字段名称
  //   * @return int
  //   */
  //  private int getSortField(String fieldName) {
  //    try {
  //      Property property = PropertysCache.getInstance().getProperty(this.entityClass, fieldName);
  //      if (property.getPropertyType().isAssignableFrom(Long.class)) {
  //        return SortField.LONG;
  //      } else if (property.getPropertyType().isAssignableFrom(Integer.class)) {
  //        return SortField.INT;
  //      } else if (property.getPropertyType().isAssignableFrom(Double.class)) {
  //        return SortField.DOUBLE;
  //      } else if (property.getPropertyType().isAssignableFrom(Float.class)) {
  //        return SortField.FLOAT;
  //      } else {
  //        return SortField.STRING;
  //      }
  //    } catch (PropertyException e) {
  //      LOGGER.error(e.getMessage(), e);
  //    }
  //    return SortField.STRING;
  //  }

  //  private void highlightObject(BuguHighlighter highlighter, Object obj) {
  //    String[] fields = highlighter.getFields();
  //    for (String fieldName : fields) {
  //      if (!fieldName.contains(".")) {
  //        Property property = null;
  //        try {
  //          property = PropertysCache.getInstance().getProperty(this.entityClass, fieldName);
  //        } catch (PropertyException ex) {
  //          LOGGER.error(ex.getMessage(), ex);
  //        }
  //        assert property != null;
  //        Object fieldValue = property.getValue(obj);
  //        if (fieldValue != null) {
  //          String result = null;
  //          try {
  //            result = highlighter.getResult(fieldName, fieldValue.toString());
  //          } catch (Exception ex) {
  //            LOGGER.error("Something is wrong when getting the highlighter result", ex);
  //          }
  //          if (!StringUtil.isEmpty(result)) {
  //            property.setValue(obj, result);
  //          }
  //        }
  //      }
  //    }
  //  }

  protected T build(T pd, Hit<T> hit, SearchResponse<T> search) {
    if (LoadEntityMode.dao == this.loadMode) {
      Serializable id = PropertysCache.getInstance().getIdProperty(entityClass).getValue(pd);
      return dataFetcher().getById(id);
    }
    return pd;
  }

  public enum LoadEntityMode {
    es,
    dao
  }
}
