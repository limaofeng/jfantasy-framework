package org.jfantasy.framework.lucene;

import org.apache.logging.log4j.LogManager;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.lucene.cache.DaoCache;
import org.jfantasy.framework.lucene.cache.IndexSearcherCache;
import org.jfantasy.framework.lucene.cache.PropertysCache;
import org.jfantasy.framework.lucene.dao.LuceneDao;
import org.jfantasy.framework.lucene.exception.IdException;
import org.jfantasy.framework.lucene.exception.PropertyException;
import org.jfantasy.framework.lucene.mapper.MapperUtil;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.reflect.Property;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lucene 查询接口
 *
 * @param <T>
 * @author 李茂峰
 * @version 1.0
 * @since 2013-5-24 上午10:57:11
 */
@SuppressWarnings("unchecked")
public abstract class BuguSearcher<T> {
    private static final Logger LOGGER = LogManager.getLogger(BuguSearcher.class);
    private Class<T> entityClass;
    private String idName;
    private LoadEntityMode loadMode;

    private LuceneDao luceneDao;

    protected BuguSearcher() {
        this(LoadEntityMode.DAO);
    }

    protected BuguSearcher(LoadEntityMode loadMode) {
        this.loadMode = loadMode;
        // 通过泛型获取需要查询的对象
        this.entityClass = (Class<T>) ClassUtil.getSuperClassGenricType(ClassUtil.getRealClass(getClass()));
        try {
            // 获取对象的主键idName
            idName = PropertysCache.getInstance().getIdProperty(this.entityClass).getName();
        } catch (IdException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private LuceneDao luceneDao() {
        if (this.luceneDao == null) {
            this.luceneDao = DaoCache.getInstance().get(this.entityClass);
        }
        return this.luceneDao;
    }

    /**
     * 打开 IndexReader
     *
     * @return IndexSearcher
     * 每次查询开始前调用
     */
    protected IndexSearcher open() {
        IndexSearcher searcher = IndexSearcherCache.getInstance().get(MapperUtil.getEntityName(this.entityClass));
        IndexReader reader = searcher.getIndexReader();
        reader.incRef();
        return searcher;
    }

    /**
     * 关闭 IndexReader
     * <p/>
     * 查询结束时调用
     *
     * @param searcher IndexSearcher
     */
    protected void close(IndexSearcher searcher) {
        try {
            searcher.getIndexReader().decRef();
        } catch (IOException ex) {
            LOGGER.error("Something is wrong when decrease the reference of IndexReader", ex);
        }
    }

    /**
     * 返回查询的结果
     *
     * @param query 查询条件
     * @param size  返回条数
     * @return List<T>
     */
    public List<T> search(Query query, int size) {
        IndexSearcher searcher = open();
        List<T> data = new ArrayList<>();
        try {
            TopDocs topDocs = searcher.search(query, size);
            for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                ScoreDoc sdoc = topDocs.scoreDocs[i];
                Document doc = searcher.doc(sdoc.doc);
                data.add(this.build(doc));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(searcher);
        }
        return data;
    }

    /**
     * 支持翻页及高亮查询
     *
     * @param pager       翻页对象
     * @param query       查询条件
     * @param highlighter 关键字高亮
     * @return Pager<T>
     */
    public Pager<T> search(Pager<T> pager, Query query, BuguHighlighter highlighter) {
        IndexSearcher searcher = open();
        int between = 0;
        try {
            TopDocs hits;
            if (pager.isOrderBySetted()) {//多重排序等HIbernateDao优化好之后再实现
                hits = searcher.search(query, pager.getCurrentPage() * pager.getPageSize(), new Sort(new SortField(pager.getOrderBy(), getFieldType(pager.getOrderBy()), Pager.SORT_DESC.equals(pager.getOrder()))));
            } else {
                hits = searcher.search(query, pager.getCurrentPage() * pager.getPageSize());
                int index = (pager.getCurrentPage() - 1) * pager.getPageSize();
                if (hits.totalHits > 0 && hits.scoreDocs.length > 0) {
                    ScoreDoc scoreDoc = index > 0 ? hits.scoreDocs[index - 1] : null;
                    hits = searcher.searchAfter(scoreDoc, query, pager.getPageSize());
                }
                between = index;
            }
            pager.reset(hits.totalHits);
            List<T> data = new ArrayList<>();
            for (int i = pager.getFirst() - between; i < hits.scoreDocs.length && hits.totalHits > 0; i++) {
                ScoreDoc sdoc = hits.scoreDocs[i];
                Document doc = searcher.doc(sdoc.doc);
                data.add(this.build(doc));
            }
            pager.reset(data);
            if (highlighter != null) {
                for (T obj : pager.getPageItems()) {
                    highlightObject(highlighter, obj);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            close(searcher);
        }
        return pager;
    }

    /**
     * @param pager   翻页对象
     * @param query   查询条件
     * @param fields  需要高亮显示的字段
     * @param keyword 高亮关键字
     * @return Pager<T>
     */
    public Pager<T> search(Pager<T> pager, Query query, String[] fields, String keyword) {
        if (StringUtil.isNotBlank(keyword)) {
            return this.search(pager, query, new BuguHighlighter(fields, keyword));
        } else {
            return this.search(pager, query);
        }
    }

    /**
     * @param pager 翻页对象
     * @param query 查询条件
     * @return Pager<T>
     */
    public Pager<T> search(Pager<T> pager, Query query) {
        return this.search(pager, query, null);
    }

    /**
     * 将javaType 转换为 SortField
     *
     * @param fieldName 字段名称
     * @return int
     */
    private int getFieldType(String fieldName) {
        try {
            Property property = PropertysCache.getInstance().getProperty(this.entityClass, fieldName);
            if (property.getPropertyType().isAssignableFrom(Long.class) || property.getPropertyType().isAssignableFrom(Date.class)) {
                return SortField.LONG;
            } else if (property.getPropertyType().isAssignableFrom(Integer.class)) {
                return SortField.INT;
            } else if (property.getPropertyType().isAssignableFrom(Double.class)) {
                return SortField.DOUBLE;
            } else if (property.getPropertyType().isAssignableFrom(Float.class)) {
                return SortField.FLOAT;
            } else {
                return SortField.STRING;
            }
        } catch (PropertyException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return SortField.STRING;
    }

    private void highlightObject(BuguHighlighter highlighter, Object obj) {
        String[] fields = highlighter.getFields();
        for (String fieldName : fields) {
            if (fieldName.contains(".")) {
                continue;
            }
            Property property;
            try {
                property = PropertysCache.getInstance().getProperty(this.entityClass, fieldName);
            } catch (PropertyException ex) {
                LOGGER.error(ex.getMessage(), ex);
                continue;
            }
            Object value = property.getValue(obj);
            if (value == null) {
                continue;
            }
            String result;
            try {
                result = highlighter.getResult(fieldName, value.toString());
            } catch (BuguHighlighter.ResultParseException ex) {
                LOGGER.error("Something is wrong when getting the highlighter result", ex);
                continue;
            }
            if (!StringUtil.isEmpty(result)) {
                property.setValue(obj, result);
            }
        }
    }

    private T build(Document doc) {
        if (LoadEntityMode.DAO == this.loadMode) {
            return this.luceneDao().getById(doc.get(idName));
        } else {
            T object = ClassUtil.newInstance(this.entityClass);
            for (Fieldable fieldable : doc.getFields()) {
                Property property;
                try {
                    property = PropertysCache.getInstance().getProperty(this.entityClass, fieldable.name());
                } catch (PropertyException e) {
                    LOGGER.error(e.getMessage(), e);
                    continue;
                }
                if (Date.class.isAssignableFrom(property.getPropertyType())) {
                    property.setValue(object, new Date(Long.valueOf(fieldable.stringValue())));
                } else {
                    property.setValue(object, fieldable.stringValue());
                }
            }
            return object;
        }
    }

    private enum LoadEntityMode {
        LUCENE, DAO
    }

}