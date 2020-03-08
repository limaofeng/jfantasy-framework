package org.jfantasy.framework.lucene.cache;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jboss.jandex.IndexWriter;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.lucene.BuguIndex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexWriterCache {
	private static final Log LOGGER = LogFactory.getLog(IndexWriterCache.class);

	private static IndexWriterCache instance = new IndexWriterCache();
	private Map<String, IndexWriter> cache;

	private IndexWriterCache() {
		this.cache = new ConcurrentHashMap<String, IndexWriter>();
	}

	public static IndexWriterCache getInstance() {
		return instance;
	}

    private PreBuiltTransportClient client;

	public void createIndex() throws IOException {
        XContentBuilder builder = jsonBuilder()
            .startObject()
            //类型名称
            .startObject("")
            //定义字段关键字
            .startObject("properties")
            .startObject("id")
            .field("type", "keyword")
            .field("index", false)
            .endObject()

            .startObject("no")
            .field("type", "keyword")
            .endObject()

            .startObject("name")
            .field("type", "text")
            .field("analyzer", "pinyin_analyzer")
            .field("search_analyzer", "ik_max_word")
            .endObject()

            .startObject("type")
            .field("type", "text")
            .field("analyzer", "pinyin_analyzer")
            .field("search_analyzer", "ik_max_word")
            .endObject()

            .startObject("stage")
            .field("type", "text")
            .field("analyzer", "pinyin_analyzer")
            .field("search_analyzer", "ik_max_word")
            .endObject()

            .startObject("organizationIdArray")
            .field("type", "text")
            .endObject()

            .startObject("finishDate")
            .field("type", "date")
            .field("index", false)
            .field("format", "yyyy-MM-dd HH:mm:ss||epoch_millis")
            .endObject()

            .startObject("suggest")
            .field("type", "completion")
            .field("analyzer", "ik_smart")
            .endObject()

            .endObject()
            .endObject()
            .endObject();

        PutMappingRequest mapping = Requests.putMappingRequest("").type("").source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
    }

    public IndexWriter get(String name) {
        if (this.cache.containsKey(name)) {
            return this.cache.get(name);
        }
//        synchronized (this) {
//            if (this.cache.containsKey(name)) {
//                return this.cache.get(name);
//            }
//            BuguIndex index = BuguIndex.getInstance();
//            IndexWriterConfig cfg = new IndexWriterConfig(index.getVersion(), index.getAnalyzer());
//            cfg.setRAMBufferSizeMB(index.getBufferSizeMB());
//            try {
//                Directory dir = FSDirectory.open(BuguIndex.getInstance().getOpenFolder("/" + name + "/"));
//                if (IndexWriter.isLocked(dir)) {
//                    IndexWriter.unlock(dir);
//                }
//                cfg.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//                this.cache.put(name, new IndexWriter(dir, cfg));
//                return this.cache.get(name);
//            } catch (IOException ex) {
//                LOGGER.error("Something is wrong when create IndexWriter for " + name, ex);
//                throw new IgnoreException(ex.getMessage(), ex);
//            }
//        }
        return null;
    }

    public Map<String, IndexWriter> getAll() {
        return this.cache;
    }

}