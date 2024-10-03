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
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.annotations.IndexEmbedBy;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.search.mapper.DataType;
import net.asany.jfantasy.framework.util.reflect.Property;

public class EmbedListFieldHandler extends AbstractFieldHandler {

  public EmbedListFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  public EmbedListFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  @Override
  public void handle(Document doc) {
    Object value = this.property.getValue(this.obj);
    if (value == null) {
      return;
    }
    List<Object> list = null;
    Class<?> clazz;
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      clazz = type.getComponentType();
      int len = Array.getLength(value);
      list = new ArrayList<>();
      for (int i = 0; i < len; i++) {
        list.add(Array.get(value, i));
      }
    } else {
      ParameterizedType paramType = this.property.getGenericType();
      Type[] types = paramType.getActualTypeArguments();
      if (types.length == 1) {
        clazz = (Class<?>) types[0];
        if (DataType.isList(type)) {
          list = (List<Object>) value;
        } else if (DataType.isSet(type)) {
          Set<?> set = (Set<?>) value;
          list = new ArrayList<>(set);
        }
      } else if (types.length == 2) {
        clazz = (Class<?>) types[1];
        Map<?, ?> map = (Map<?, ?>) value;
        list = new ArrayList<>(map.values());
      } else {
        return;
      }
    }
    if ((list != null) && (!list.isEmpty())) {
      for (Property p : PropertysCache.getInstance().filter(clazz, IndexEmbedBy.class)) {
        FieldHandler handler = new EmbedByFieldHandler(this.obj.getClass(), list, p, this.prefix);
        handler.handle(doc);
      }
    }
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {
    Class<?> clazz;
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      clazz = type.getComponentType();
    } else {
      ParameterizedType paramType = this.property.getGenericType();
      Type[] types = paramType.getActualTypeArguments();
      if (types.length == 1) {
        clazz = (Class<?>) types[0];
      } else if (types.length == 2) {
        clazz = (Class<?>) types[1];
      } else {
        return;
      }
    }
    for (Property p : PropertysCache.getInstance().filter(clazz, IndexEmbedBy.class)) {
      FieldHandler handler = new EmbedByFieldHandler(p, this.prefix);
      handler.handle(typeMapping);
    }
  }
}
