package org.jfantasy.framework.lucene.backend;

import org.jfantasy.framework.lucene.cache.IndexSearcherCache;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IndexReopenTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(IndexReopenTask.class);
    private static final Lock reopenLock = new ReentrantLock();

    @Override
    public void run() {
        if (!reopenLock.tryLock()) {
            return;
        }
        try {
            IndexSearcherCache searcherCache = IndexSearcherCache.getInstance();
            for (Map.Entry<String, IndexSearcher> entry : searcherCache.getAll().entrySet()) {
                IndexSearcher searcher = entry.getValue();
                IndexSearcher newSearcher = reopen(searcher);
                if (newSearcher != null) {
                    searcherCache.put(entry.getKey(), newSearcher);
                }
            }
        } finally {
            reopenLock.unlock();
        }
    }

    private IndexSearcher reopen(IndexSearcher searcher) {
        try {
            IndexReader reader = searcher.getIndexReader();
            IndexReader newReader = IndexReader.openIfChanged(reader);//NOSONAR
            if (newReader != reader) {
                reader.decRef();
                return new IndexSearcher(newReader);
            }
            return null;
        } catch (IOException ex) {
            LOGGER.error("Something is wrong when reopen the Lucene IndexReader", ex);
            return null;
        }
    }

}