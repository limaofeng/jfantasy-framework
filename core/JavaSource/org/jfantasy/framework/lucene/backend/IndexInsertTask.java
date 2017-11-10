package org.jfantasy.framework.lucene.backend;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.jfantasy.framework.lucene.cache.IndexWriterCache;
import org.jfantasy.framework.lucene.mapper.MapperUtil;

import java.io.IOException;

public class IndexInsertTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(IndexInsertTask.class);
    private Object entity;

    public IndexInsertTask(Object entity) {
        this.entity = entity;
    }

    @Override
    public void run() {
        Class<?> clazz = this.entity.getClass();
        String name = MapperUtil.getEntityName(clazz);
        IndexWriterCache cache = IndexWriterCache.getInstance();
        IndexWriter writer = cache.get(name);
        Document doc = new Document();
        IndexCreator creator = new IndexCreator(this.entity, "");
        creator.create(doc);
        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            LOGGER.error("IndexWriter can not add a document to the lucene index", ex);
        }
    }
}