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
