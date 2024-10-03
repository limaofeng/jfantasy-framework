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
package net.asany.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.exception.IdException;
import net.asany.jfantasy.framework.util.reflect.Property;

public class IdFieldHandler extends AbstractFieldHandler {

  public IdFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  public IdFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  @Override
  public void handle(Document doc) {
    doc.setId(getEntityId());
    doc.add(fieldName, getEntityId());
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {
    String fieldName = this.prefix + this.property.getName();
    typeMapping.properties(
        fieldName, builder -> builder.keyword(builder1 -> builder1.store(true).index(false)));
  }

  private String getEntityId() {
    Object id = this.property.getValue(this.obj);
    if (id == null) {
      throw new IdException("要索引的对象@Id字段不能为空");
    }
    return String.valueOf(id);
  }
}
