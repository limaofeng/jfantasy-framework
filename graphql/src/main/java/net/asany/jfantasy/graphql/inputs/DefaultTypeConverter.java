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
package net.asany.jfantasy.graphql.inputs;

import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;

public class DefaultTypeConverter<T> implements TypeConverter<T> {

  private final Class<T> type;

  public DefaultTypeConverter(Class<T> type) {
    this.type = type;
  }

  @Override
  public T convert(Object value) {
    if (type.isEnum()) {
      if (value instanceof Enum) {
        //noinspection unchecked
        return (T) value;
      }
      //noinspection unchecked,rawtypes
      return (T) Enum.valueOf((Class<Enum>) type, (String) value);
    }
    return ReflectionUtils.convert(value, type);
  }
}
