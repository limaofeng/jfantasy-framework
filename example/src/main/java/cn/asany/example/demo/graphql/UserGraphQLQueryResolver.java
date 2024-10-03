/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.asany.example.demo.graphql;

import cn.asany.example.demo.graphql.inputs.UserWhereInput;
import cn.asany.example.demo.graphql.types.UserConnection;
import cn.asany.example.demo.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.graphql.util.Kit;
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

  public UserConnection demoUsers(
      UserWhereInput where,
      int page,
      int pageSize,
      Sort sort,
      DataFetchingEnvironment environment) {
    Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
    where = ObjectUtil.defaultValue(where, new UserWhereInput());
    return Kit.connection(userService.findPage(pageable, where.toFilter()), UserConnection.class);
  }
}
