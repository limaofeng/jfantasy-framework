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

import jakarta.persistence.Id;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.Date;
import net.asany.jfantasy.framework.search.annotations.FieldType;
import net.asany.jfantasy.framework.search.annotations.IndexProperty;
import net.asany.jfantasy.framework.search.mapper.DataType;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.reflect.Property;

public abstract class AbstractFieldHandler implements FieldHandler {
  protected static final String JOIN = ";";
  protected Object obj;
  protected Property property;
  protected String prefix;
  protected IndexProperty indexProperty;
  protected FieldType fieldType;
  protected String fieldName;

  protected AbstractFieldHandler(Property property, String prefix) {
    this.property = property;
    this.prefix = prefix;
    this.indexProperty = this.property.getAnnotation(IndexProperty.class);

    boolean isId = this.indexProperty == null && this.property.getAnnotation(Id.class) != null;
    Class<?> type = this.property.getPropertyType();
    if (isId) {
      this.fieldType = FieldType.Keyword;
    } else if (type.isArray()) {
      this.fieldType = FieldType.Text;
    } else {
      this.fieldType =
          FieldType.Auto == indexProperty.type()
              ? DataType.getFieldType(type)
              : indexProperty.type();
    }
    this.fieldName =
        this.prefix
            + StringUtil.defaultValue(
                indexProperty == null ? "" : indexProperty.name(), this.property.getName());
  }

  protected AbstractFieldHandler(Object obj, Property property, String prefix) {
    this(property, prefix);
    this.obj = obj;
  }

  protected String getArrayString(Object value, Class<?> type) {
    StringBuilder sb = new StringBuilder();
    if (DataType.isDate(type)) {
      Date[] arr = (Date[]) value;
      for (Date e : arr) {
        sb.append(e.getTime()).append(";");
      }
    } else if (DataType.isTimestamp(type)) {
      Timestamp[] arr = (Timestamp[]) value;
      for (Timestamp e : arr) {
        sb.append(e.getTime()).append(";");
      }
    } else {
      int len = Array.getLength(value);
      for (int i = 0; i < len; i++) {
        sb.append(Array.get(value, i).toString()).append(";");
      }
    }
    return sb.toString();
  }
}
