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

import net.asany.jfantasy.framework.search.annotations.Compare;
import net.asany.jfantasy.framework.search.annotations.IndexFilter;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.util.reflect.Property;

public class IndexFilterChecker {

  private final Object entity;

  public IndexFilterChecker(Object entity) {
    this.entity = entity;
  }

  public boolean needIndex() {
    Class<?> clazz = this.entity.getClass();
    for (Property p : PropertysCache.getInstance().filter(clazz, IndexFilter.class)) {
      IndexFilter filter = p.getAnnotation(IndexFilter.class);
      Compare compare = filter.compare();
      String value = filter.value();
      CompareChecker checker = new CompareChecker(this.entity);
      if (!checker.isFit(p, compare, value)) {
        return false;
      }
    }
    return true;
  }
}
