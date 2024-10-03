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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@JsonFilter("colorsFilter")
public class CustomBeanPropertyFilter extends SimpleBeanPropertyFilter {

  private Map<Class<?>, Set<String>> includeMap = new ConcurrentHashMap<>();

  private Map<Class<?>, Set<String>> filterMap = new ConcurrentHashMap<>();

  public void include(Class<?> type, String[] fields) {
    addToMap(includeMap, type, fields);
  }

  public void filter(Class<?> type, String[] fields) {
    addToMap(filterMap, type, fields);
  }

  private void addToMap(Map<Class<?>, Set<String>> map, Class<?> type, String[] fields) {
    Set<String> fieldSet = map.getOrDefault(type, new HashSet<>());
    fieldSet.addAll(Arrays.asList(fields));
    map.put(type, fieldSet);
  }

  @Override
  public void serializeAsField(
      Object pojo, JsonGenerator jgen, SerializerProvider prov, PropertyWriter writer)
      throws Exception {
    if (apply(pojo.getClass(), writer.getName())) {
      writer.serializeAsField(pojo, jgen, prov);
    } else if (!jgen.canOmitFields()) {
      writer.serializeAsOmittedField(pojo, jgen, prov);
    }
  }

  private boolean apply(Class<?> type, String name) {
    Set<String> includeFields = includeMap.get(type);
    Set<String> filterFields = filterMap.get(type);
    if (includeFields != null && includeFields.contains(name)) {
      return true;
    } else if (filterFields != null && !filterFields.contains(name)) {
      return true;
    } else if (includeFields == null && filterFields == null) {
      return true;
    }
    return false;
  }
}
