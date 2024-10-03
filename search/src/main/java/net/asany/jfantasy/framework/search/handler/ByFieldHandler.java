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

import java.util.List;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.util.reflect.Property;

public abstract class ByFieldHandler extends PropertyFieldHandler {

  protected ByFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  protected ByFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  protected void processList(
      List<?> objList, Document doc, boolean analyze, boolean store, double boost) {
    StringBuilder sb = new StringBuilder();
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      for (Object o : objList) {
        Object value = this.property.getValue(o);
        if (value != null) {
          sb.append(getArrayString(value, type.getComponentType())).append(";");
        }
      }
    } else {
      for (Object o : objList) {
        Object value = this.property.getValue(o);
        if (value != null) {
          sb.append(value).append(";");
        }
      }
    }
    doc.add(fieldName, sb.toString());
  }
}
