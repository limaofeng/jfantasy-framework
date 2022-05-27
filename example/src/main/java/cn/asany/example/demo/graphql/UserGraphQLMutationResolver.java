package cn.asany.example.demo.graphql;

import cn.asany.example.demo.converter.UserConverter;
import cn.asany.example.demo.domain.User;
import cn.asany.example.demo.graphql.inputs.UserCreateInput;
import cn.asany.example.demo.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/** @author limaofeng */
@Component
public class UserGraphQLMutationResolver implements GraphQLMutationResolver {

  private final UserService userService;
  private final UserConverter userConverter;

  public UserGraphQLMutationResolver(UserService userService, UserConverter userConverter) {
    this.userService = userService;
    this.userConverter = userConverter;
  }

  public User createUser(@Validated UserCreateInput input) {
    User user = userConverter.toUser(input);
    return userService.save(user);
  }
}
