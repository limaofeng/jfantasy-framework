/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.search.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import java.io.IOException;
import java.util.Map;
import net.asany.jfantasy.framework.search.CuckooIndex;
import net.asany.jfantasy.framework.search.annotations.BoostSwitch;
import net.asany.jfantasy.framework.search.annotations.Indexed;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.search.dao.CuckooDao;
import net.asany.jfantasy.framework.search.handler.FieldHandler;
import net.asany.jfantasy.framework.search.handler.FieldHandlerFactory;
import net.asany.jfantasy.framework.search.query.Query;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * ElasticCuckooIndex
 *
 * @author limaofeng
 */
public class ElasticCuckooIndex implements CuckooIndex {

  private final Indexed indexed;
  private final Class<?> indexClass;
  private final ElasticsearchConnection connection;
  private final IndexWriter indexWriter;

  public ElasticCuckooIndex(
      Class<?> clazz, CuckooDao cuckooDao, ElasticsearchConnection connection, int batchSize)
      throws IOException {
    this.indexed = clazz.getAnnotation(Indexed.class);
    this.indexClass = clazz;
    this.connection = connection;

    this.indexWriter = new ElasticIndexWriter(this, this.connection, batchSize);

    this.initialize();
  }

  public TypeMapping.Builder initProperties(TypeMapping.Builder typeMapping) {
    net.asany.jfantasy.framework.util.reflect.Property[] properties =
        PropertysCache.getInstance().get(this.indexClass);
    String prefix = "";
    for (net.asany.jfantasy.framework.util.reflect.Property p : properties) {
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
    if (!indexed.createIndex()) {
      return;
    }

    String indexName =
        StringUtil.defaultValue(
            indexed.indexName(), () -> StringUtil.snakeCase(indexClass.getName()));

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
  public Class<?> getIndexClass() {
    return this.indexClass;
  }

  @Override
  public IndexWriter getIndexWriter() {
    return this.indexWriter;
  }

  @Override
  public String getIndexName() {
    return this.indexed.indexName();
  }

  @Override
  public <T> SmartSearcher<T> searcher(Query query) {
    return new SmartSearcherImpl<>(this, this.connection, query);
  }
}
