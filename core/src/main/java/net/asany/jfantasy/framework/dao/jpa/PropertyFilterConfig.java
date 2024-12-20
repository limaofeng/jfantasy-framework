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
package net.asany.jfantasy.framework.dao.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.framework.dao.MatchType;

@Data
@Builder
public class PropertyFilterConfig {

  public void doWithProperty(
      Class<?> entityClass,
      String property,
      MatchType matchType,
      PropertyFilterBuilder.PropertyPredicateCallback<List<PropertyPredicate>> callback) {
    Map<String, TypeConverter<?>> typeConverterMap =
        PropertyFilterBuilder.initDefaultConverters(entityClass);
    //noinspection rawtypes
    Map<String, PropertyDefinition> propertyDefinitionMap =
        PropertyFilterBuilder.CUSTOM_PROPERTIES.computeIfAbsent(
            entityClass, (clazz) -> new HashMap<>());

    //noinspection unchecked
    PropertyDefinition<List<PropertyPredicate>> definition =
        (PropertyDefinition<List<PropertyPredicate>>)
            propertyDefinitionMap.computeIfAbsent(
                property,
                (key) -> PropertyDefinition.<List<PropertyPredicate>>builder().name(key).build());
    definition.getPredicates().putIfAbsent(matchType.name(), callback);
  }
}
