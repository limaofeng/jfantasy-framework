package cn.asany.example.demo.graphql;

import cn.asany.example.demo.domain.IUserSettings;
import cn.asany.example.demo.domain.User;
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.stereotype.Component;

@Component
public class UserGraphQLResolver implements GraphQLResolver<User> {
  public IUserSettings settings(User user) {
    return user.getSetting();
  }
}
