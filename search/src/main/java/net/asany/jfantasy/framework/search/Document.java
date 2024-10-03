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
package net.asany.jfantasy.framework.search;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class Document {
  @Getter private final Map<String, Object> attrs = new HashMap<>();
  @Getter private final String indexName;
  @Getter @Setter private String id;

  public Document(String indexName) {
    this.indexName = indexName;
  }

  public void setBoost(float fit) {}

  public void add(String name, Object value) {
    this.attrs.put(name, value);
  }
}
