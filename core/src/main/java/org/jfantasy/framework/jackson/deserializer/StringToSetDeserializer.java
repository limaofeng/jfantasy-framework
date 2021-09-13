package org.jfantasy.framework.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
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
    if (jp.isExpectedStartArrayToken()) {
      Set objects = new HashSet();
      String value = jp.nextTextValue();
      do {
        if (StringUtil.isNotBlank(value)) {
          objects.add(itemDeserialize(value));
        }
        value = jp.nextTextValue();
      } while (StringUtil.isNotBlank(value));
      return objects;
    }
    return null;
  }

  /**
   * 每一项的转换逻辑
   *
   * @param text 字符串
   * @return T
   */
  public abstract T itemDeserialize(String text);
}
