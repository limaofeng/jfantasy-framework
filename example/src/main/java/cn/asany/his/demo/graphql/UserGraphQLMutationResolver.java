package cn.asany.his.demo.graphql;

import cn.asany.his.demo.bean.User;
import cn.asany.his.demo.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author limaofeng
 */
@Component
public class UserGraphQLMutationResolver implements GraphQLMutationResolver {

    private final UserService userService;

    public UserGraphQLMutationResolver(UserService userService) {
        this.userService = userService;
    }

    public User createUser(@Validated User user) {
        return userService.save(user);
    }

}
