package org.jfantasy.graphql.util;

import java.util.List;
import lombok.*;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.types.BaseConnection;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserConnection extends BaseConnection<UserConnection.UserDetailsEdge, String> {

  private List<UserDetailsEdge> edges;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserDetailsEdge implements Edge<String> {
    private String cursor;
    private String node;
  }
}
