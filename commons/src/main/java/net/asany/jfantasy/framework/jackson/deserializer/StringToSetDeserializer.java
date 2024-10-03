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
package net.asany.jfantasy.framework.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * 字符串转集合
 *
 * @author limaofeng
 * @param <T>
 */
public abstract class StringToSetDeserializer<T> extends JsonDeserializer<Set<T>> {
  @Override
  public Set<T> deserialize(JsonParser jp, DeserializationContext context) throws IOException {
    JsonNode node = jp.readValueAsTree();
    if (!node.isArray()) {
      return null;
    }
    Set<T> objects = new HashSet<>();
    ArrayNode arrayNode = (ArrayNode) node;
    for (JsonNode elementNode : arrayNode) {
      String value = elementNode.toString();
      if (StringUtil.isNotBlank(value)) {
        objects.add(itemDeserialize(value));
      }
    }
    return objects;
  }

  /**
   * 每一项的转换逻辑
   *
   * @param text 字符串
   * @return T
   */
  public abstract T itemDeserialize(String text);
}
