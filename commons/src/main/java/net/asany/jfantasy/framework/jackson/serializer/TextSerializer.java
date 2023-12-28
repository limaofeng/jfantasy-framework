package net.asany.jfantasy.framework.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import net.asany.jfantasy.framework.util.common.StringUtil;

public class TextSerializer extends JsonSerializer<String> {

  @Override
  public void serialize(String value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeString(StringUtil.escapeHtml(value));
  }
}
