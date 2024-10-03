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
package net.asany.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;

public class ColorsSerializer {

  private ThreadLocal<ObjectMapper> mapper = ThreadLocal.withInitial(ObjectMapper::new);

  private FilteredMixinFilter propertyFilter = new FilteredMixinFilter();

  public void filter(Class<?> clazz, String include, String filter) {
    if (clazz == null) {
      return;
    }
    if (StringUtils.isNotBlank(include)) {
      propertyFilter.includes(clazz, include.split(","));
    }
    if (StringUtils.isNotBlank(filter)) {
      propertyFilter.excludes(clazz, filter.split(","));
    }
    mapper.get().addMixIn(clazz, FilteredMixinFilter.class);
  }

  public String toJson(Object object) throws JsonProcessingException {
    SimpleFilterProvider provider = new SimpleFilterProvider();
    provider.setDefaultFilter(propertyFilter);
    return mapper.get().setFilterProvider(provider).writeValueAsString(object);
  }

  public void filter(JsonResultFilter jsonResultFilter) {
    this.filter(jsonResultFilter.type(), jsonResultFilter.include(), jsonResultFilter.filter());
  }
}
