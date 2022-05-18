package org.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import java.io.IOException;
import java.util.Map;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.annotations.BoostSwitch;
import org.jfantasy.framework.search.annotations.Document;
import org.jfantasy.framework.search.cache.PropertysCache;
import org.jfantasy.framework.search.dao.CuckooDao;
import org.jfantasy.framework.search.handler.FieldHandler;
import org.jfantasy.framework.search.handler.FieldHandlerFactory;
import org.jfantasy.framework.util.common.StringUtil;

public class ElasticCuckooIndex implements CuckooIndex {

  private final Document document;
  private final Class indexClass;
  private final ElasticsearchConnection connection;
  private final IndexWriter indexWriter;
  private final IndexSearcher indexSearcher;

  public ElasticCuckooIndex(
      Class<?> clazz, CuckooDao cuckooDao, ElasticsearchConnection connection, int batchSize)
      throws IOException {
    this.document = clazz.getAnnotation(Document.class);
    this.indexClass = clazz;
    this.connection = connection;

    this.indexWriter = new ElasticIndexWriter(this, this.connection, batchSize);
    this.indexSearcher = new ElasticIndexSearcher(this, this.connection);

    this.initialize();
  }

  public TypeMapping.Builder initProperties(TypeMapping.Builder typeMapping) {
    org.jfantasy.framework.util.reflect.Property[] properties =
        PropertysCache.getInstance().get(this.indexClass);
    String prefix = "";
    for (org.jfantasy.framework.util.reflect.Property p : properties) {
      BoostSwitch bs = p.getAnnotation(BoostSwitch.class);
      if (FieldHandlerFactory.isHandler(p)) {
        FieldHandler handler = FieldHandlerFactory.create(p, prefix);
        if (handler != null) {
          handler.handle(typeMapping);
        }
      }
    }
    return typeMapping;
  }

  private void initialize() throws IOException {
    if (!document.createIndex()) {
      return;
    }

    String indexName =
        StringUtil.defaultValue(
            document.indexName(), () -> StringUtil.snakeCase(indexClass.getName()));

    ElasticsearchClient client = connection.getClient();

    BooleanResponse response =
        client.indices().exists(ExistsRequest.of(builder -> builder.index(indexName)));
    if (response.value()) {
      TypeMapping.Builder builder = new TypeMapping.Builder();
      Map<String, Property> propertyMap = initProperties(builder).build().properties();
      PutMappingRequest request =
          new PutMappingRequest.Builder().index(indexName).properties(propertyMap).build();
      client.indices().putMapping(request);
    } else {
      CreateIndexRequest request =
          new CreateIndexRequest.Builder().index(indexName).mappings(this::initProperties).build();
      client.indices().create(request);
    }
  }

  @Override
  public Class getDocumentClass() {
    return this.indexClass;
  }

  @Override
  public Document getDocument() {
    return this.document;
  }

  @Override
  public IndexWriter getIndexWriter() {
    return this.indexWriter;
  }

  @Override
  public String getIndexName() {
    return this.getDocument().indexName();
  }

  @Override
  public IndexSearcher getIndexSearcher() {
    return this.indexSearcher;
  }
}
