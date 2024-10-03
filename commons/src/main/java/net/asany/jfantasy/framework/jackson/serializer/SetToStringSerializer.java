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
package net.asany.jfantasy.framework.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Set;

/**
 * 集合转字符串
 *
 * @author limaofeng
 * @param <T>
 */
public abstract class SetToStringSerializer<T> extends JsonSerializer<Set<T>> {
  @Override
  public void serialize(Set<T> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartArray();
    for (T item : value) {
      gen.writeString(itemSerialize(item));
    }
    gen.writeEndArray();
  }

  /**
   * 对象转字符串
   *
   * @param object 对象
   * @return String
   */
  public abstract String itemSerialize(T object);
}
