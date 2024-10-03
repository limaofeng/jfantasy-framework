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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.HashMap;
import java.util.Map;

public class ResultResourceSupport<T> {

  private final T model;
  private final Map<String, Object> properties = new HashMap<>();

  @JsonCreator
  public ResultResourceSupport(T model) {
    this.model = model;
  }

  @JsonAnyGetter
  public Map<String, Object> getProperties() {
    return this.properties;
  }

  @JsonUnwrapped
  public T getModel() {
    return this.model;
  }

  @JsonAnySetter
  public void set(String key, Object value) {
    this.properties.put(key, value);
  }
}
