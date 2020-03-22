package com.thuni.his.demo.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.thuni.his.demo.bean.User;
import com.thuni.his.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
public class UserGraphQLMutationResolver implements GraphQLMutationResolver {

    @Autowired
    private UserService userService;

    public User createUser(@Validated User user) {
        return userService.save(user);
    }

}
