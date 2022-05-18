package cn.asany.his.demo.graphql;

import cn.asany.his.demo.bean.User;
import cn.asany.his.demo.graphql.inputs.UserFilter;
import cn.asany.his.demo.graphql.types.UserConnection;
import cn.asany.his.demo.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.jfantasy.framework.dao.OrderBy;
import org.jfantasy.framework.dao.Pagination;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.graphql.util.Kit;
import org.springframework.stereotype.Component;

/** @author limaofeng */
@Component
public class UserGraphQLQueryResolver implements GraphQLQueryResolver {

  private final UserService userService;

  public UserGraphQLQueryResolver(UserService userService) {
    this.userService = userService;
  }

  public UserConnection users(UserFilter filter, int page, int pageSize, OrderBy orderBy) {
    Pagination<User> pager = new Pagination<>(page, pageSize, orderBy);
    filter = ObjectUtil.defaultValue(filter, new UserFilter());
    return Kit.connection(userService.findPager(pager, filter.build()), UserConnection.class);
  }
}
