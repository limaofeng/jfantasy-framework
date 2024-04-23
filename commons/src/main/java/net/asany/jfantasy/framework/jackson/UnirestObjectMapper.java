package net.asany.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import kong.unirest.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnirestObjectMapper implements ObjectMapper {

  private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

  public UnirestObjectMapper(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public <T> T readValue(String value, Class<T> valueType) {
    try {
      return this.objectMapper.readValue(value, valueType);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String writeValue(Object value) {
    try {
      return this.objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }
}
