package net.asany.jfantasy.graphql.gateway.error;

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
import net.asany.jfantasy.framework.spring.mvc.http.FieldValidationError;

@Data
public class DataFetchGraphQLError {
  private String message;

  @JsonDeserialize(using = SourceLocationDeserializer.class)
  private List<SourceLocation> locations;

  private List<Object> path;
  private Map<String, Object> extensions;
  private String timestamp;

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
