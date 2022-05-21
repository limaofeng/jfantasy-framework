package cn.asany.example.demo.graphql;

import cn.asany.example.demo.graphql.inputs.UserFilter;
import cn.asany.example.demo.graphql.types.UserConnection;
import cn.asany.example.demo.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.graphql.util.Kit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/** @author limaofeng */
@Component
public class UserGraphQLQueryResolver implements GraphQLQueryResolver {

  private final UserService userService;

  public UserGraphQLQueryResolver(UserService userService) {
    this.userService = userService;
  }

  public UserConnection users(UserFilter filter, int page, int pageSize, Sort sort) {
    Pageable pageable = PageRequest.of(page, pageSize, sort);
    filter = ObjectUtil.defaultValue(filter, new UserFilter());
    return Kit.connection(userService.findPage(pageable, filter.build()), UserConnection.class);
  }
}
