package org.jfantasy.framework.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.jfantasy.framework.util.regexp.RegexpUtil;

public abstract class StringReplaceSerializer extends JsonSerializer<String> {

  private final String regex;
  private final String replacement;

  public StringReplaceSerializer(String regex, String replacement) {
    this.regex = regex;
    this.replacement = replacement;
  }

  @Override
  public void serialize(String value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    jgen.writeString(RegexpUtil.replace(value, regex, replacement));
  }
}
