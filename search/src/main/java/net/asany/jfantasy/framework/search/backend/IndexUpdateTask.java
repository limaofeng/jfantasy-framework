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
package net.asany.jfantasy.framework.search.backend;

import java.io.IOException;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.search.CuckooIndex;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.cache.IndexCache;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.search.elastic.IndexWriter;
import net.asany.jfantasy.framework.util.reflect.Property;

@Slf4j
public class IndexUpdateTask implements Runnable {
  private final Object entity;

  public IndexUpdateTask(Object entity) {
    this.entity = entity;
  }

  @Override
  public void run() {
    Class<?> clazz = this.entity.getClass();
    CuckooIndex cuckooIndex = IndexCache.getInstance().get(clazz);
    IndexWriter writer = cuckooIndex.getIndexWriter();

    Document doc = new Document(cuckooIndex.getIndexName());

    IndexCreator creator = new IndexCreator(this.entity, "");
    creator.create(doc);

    Property property = PropertysCache.getInstance().getIdProperty(clazz);
    Serializable id = property.getValue(this.entity);
    try {
      writer.updateDocument(id, doc);
    } catch (IOException ex) {
      log.error("IndexWriter can not update the document", ex);
    }
  }
}
