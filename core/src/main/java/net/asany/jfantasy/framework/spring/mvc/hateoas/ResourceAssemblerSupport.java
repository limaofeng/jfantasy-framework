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
package net.asany.jfantasy.framework.spring.mvc.hateoas;

import java.util.ArrayList;
import java.util.List;
import net.asany.jfantasy.framework.dao.Page;

public abstract class ResourceAssemblerSupport<T, R extends ResultResourceSupport> {

  protected ResultResourceSupport<T> instantiateResource(T entity) {
    return new ResultResourceSupport<>(entity);
  }

  protected ResultResourceSupport createResourceWithId(T entity) {
    return instantiateResource(entity);
  }

  public abstract ResultResourceSupport toResource(T entity);

  public List<ResultResourceSupport> toResources(List<T> items) {
    List<ResultResourceSupport> supports = new ArrayList<>(items.size());
    for (T item : items) {
      supports.add(toResource(item));
    }
    return supports;
  }

  public Page<ResultResourceSupport> toResources(Page<T> page) {
    return Page.of(page, this.toResources(page.getPageItems()));
  }
}
