package org.jfantasy.framework.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.jfantasy.framework.util.common.StringUtil;

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
