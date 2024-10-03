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
package net.asany.jfantasy.framework.search.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.search.CuckooIndex;
import net.asany.jfantasy.framework.search.exception.NoIndexException;

@Slf4j
public class IndexCache {

  private static final IndexCache instance = new IndexCache();
  private final Map<Class, CuckooIndex> cache;

  private IndexCache() {
    this.cache = new ConcurrentHashMap<>();
  }

  public static IndexCache getInstance() {
    return instance;
  }

  public CuckooIndex get(Class indexedClass) {
    if (!this.cache.containsKey(indexedClass)) {
      throw new NoIndexException(indexedClass.getName() + "  索引未找到");
    }
    return this.cache.get(indexedClass);
  }

  public void put(Class indexClass, CuckooIndex index) {
    this.cache.put(indexClass, index);
  }

  public Map<Class, CuckooIndex> getAll() {
    return this.cache;
  }
}
