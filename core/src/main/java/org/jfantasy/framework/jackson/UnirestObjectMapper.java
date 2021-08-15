package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnirestObjectMapper implements ObjectMapper {

  private static final Log LOG = LogFactory.getLog(UnirestObjectMapper.class);

  private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

  public UnirestObjectMapper(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public <T> T readValue(String value, Class<T> valueType) {
    try {
      return this.objectMapper.readValue(value, valueType);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String writeValue(Object value) {
    try {
      return this.objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }
}
