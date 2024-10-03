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
import net.asany.jfantasy.framework.search.cache.IndexCache;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.search.elastic.IndexWriter;
import net.asany.jfantasy.framework.util.reflect.Property;

/**
 * 删除索引
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-26 下午09:09:59
 */
@Slf4j
public class IndexRemoveTask implements Runnable {
  private final Class<?> clazz;
  private final Serializable id;

  public IndexRemoveTask(Class<?> clazz, Serializable id) {
    this.clazz = clazz;
    this.id = id;
  }

  @Override
  public void run() {
    CuckooIndex cuckooIndex = IndexCache.getInstance().get(this.clazz);
    IndexWriter writer = cuckooIndex.getIndexWriter();
    Property property = PropertysCache.getInstance().getIdProperty(this.clazz);
    try {
      writer.deleteDocument(this.id);
    } catch (IOException e) {
      log.error("IndexWriter can not delete a document from the index", e);
    }
  }
}
