package net.asany.jfantasy.graphql.client;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPayload {
  private String query;
  private Map<String, Object> variables;
  private String operationName;
}
