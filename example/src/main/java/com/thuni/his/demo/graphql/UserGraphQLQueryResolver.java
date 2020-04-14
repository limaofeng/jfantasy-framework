package com.thuni.his.demo.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.thuni.his.demo.bean.User;
import com.thuni.his.demo.graphql.inputs.UserFilter;
import com.thuni.his.demo.graphql.types.UserConnection;
import com.thuni.his.demo.service.UserService;
import org.jfantasy.framework.dao.OrderBy;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.graphql.util.Kit;
import org.springframework.stereotype.Component;

/**
 * @author limaofeng
 */
@Component
public class UserGraphQLQueryResolver implements GraphQLQueryResolver {

    private final UserService userService;

    public UserGraphQLQueryResolver(UserService userService) {
        this.userService = userService;
    }

    public UserConnection users(UserFilter filter, int page, int pageSize, OrderBy orderBy) {
        Pager<User> pager = new Pager<>(page, pageSize, orderBy);
        filter = ObjectUtil.defaultValue(filter, new UserFilter());
        return Kit.connection(userService.findPager(pager, filter.build()), UserConnection.class);
    }

}
