package cn.asany.his.demo.graphql.types;

import cn.asany.his.demo.bean.User;
import lombok.*;
import org.jfantasy.graphql.Edge;
import org.jfantasy.graphql.types.BaseConnection;

import java.util.List;

/**
 * @author limaofeng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserConnection extends BaseConnection<UserConnection.UserEdge> {

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
