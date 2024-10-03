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
package net.asany.jfantasy.graphql.client.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import graphql.language.SourceLocation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import net.asany.jfantasy.framework.error.FieldValidationError;

@Data
public class DataFetchGraphQLError {
  private String message;

  @JsonDeserialize(using = SourceLocationDeserializer.class)
  private List<SourceLocation> locations;

  private List<Object> path;
  private Map<String, Object> extensions;

  @JsonProperty("errors")
  private List<FieldValidationError> fieldErrors;

  private Map<String, Object> data;

  public static class SourceLocationDeserializer extends JsonDeserializer<List<SourceLocation>> {

    @Override
    public List<SourceLocation> deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException {
      JsonNode arrayNode = jp.getCodec().readTree(jp);
      if (arrayNode.isNull()) {
        return null;
      }
      List<SourceLocation> locations = new ArrayList<>();
      for (int i = 0; i < arrayNode.size(); i++) {
        JsonNode node = arrayNode.get(i);
        int line = node.get("line").asInt();
        int column = node.get("column").asInt();
        locations.add(new SourceLocation(line, column));
      }
      return locations;
    }
  }
}
