package cn.asany.example.demo.graphql.types;

import cn.asany.example.demo.domain.User;
import java.util.List;
import lombok.*;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.types.BaseConnection;

/** @author limaofeng */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserConnection extends BaseConnection<UserConnection.UserEdge, User> {

  private List<UserEdge> edges;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserEdge implements Edge<User> {
    private String cursor;
    private User node;
  }
}
