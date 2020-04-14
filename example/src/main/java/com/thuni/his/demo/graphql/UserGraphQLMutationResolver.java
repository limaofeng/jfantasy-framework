package com.thuni.his.demo.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.thuni.his.demo.bean.User;
import com.thuni.his.demo.service.UserService;
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
