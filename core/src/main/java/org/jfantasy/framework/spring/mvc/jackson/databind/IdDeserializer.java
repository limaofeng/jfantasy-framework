package org.jfantasy.framework.spring.mvc.jackson.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.jfantasy.framework.util.common.NumberUtil;

public class IdDeserializer extends JsonDeserializer<Long> {

  @Override
  public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String id = p.getValueAsString();
    return NumberUtil.toLong(id, 16) / 20170302;
  }
}
