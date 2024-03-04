package cn.asany.example.demo.graphql;

import cn.asany.example.demo.graphql.inputs.UserWhereInput;
import cn.asany.example.demo.graphql.types.UserConnection;
import cn.asany.example.demo.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.graphql.util.Kit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * 用户查询
 *
 * @author limaofeng
 */
@Component
public class UserGraphQLQueryResolver implements GraphQLQueryResolver {

  private final UserService userService;

  public UserGraphQLQueryResolver(UserService userService) {
    this.userService = userService;
  }

  public UserConnection users(UserWhereInput where, int page, int pageSize, Sort sort) {
    Pageable pageable = PageRequest.of(page, pageSize, sort);
    where = ObjectUtil.defaultValue(where, new UserWhereInput());
    return Kit.connection(userService.findPage(pageable, where.toFilter()), UserConnection.class);
  }
}
