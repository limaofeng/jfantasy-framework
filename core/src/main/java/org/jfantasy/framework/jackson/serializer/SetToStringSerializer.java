package org.jfantasy.framework.jackson.serializer;

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
