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
package cn.asany.example.demo.service;

import cn.asany.example.demo.dao.UserDao;
import cn.asany.example.demo.domain.User;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import net.asany.jfantasy.framework.dao.datasource.DataSourceContextHolder;
import net.asany.jfantasy.framework.dao.jpa.PropertyFilter;
import net.asany.jfantasy.framework.log.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author limaofeng
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

  private final UserDao userDao;

  @Autowired(required = false)
  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  /**
   * 保存用户
   *
   * @param user 用户对象
   */
  @Log(text = "保存用户: {name}")
  public User save(User user) {
    try {
      DataSourceContextHolder.addDataSourceRoute("test1");
      return this.userDao.save(user);
    } finally {
      DataSourceContextHolder.removeDataSourceRoute();
    }
  }

  public User update(String id, boolean merge, User user) {
    user.setId(id);
    return this.userDao.update(user, merge);
  }

  public Page<User> findPage(Pageable pageable, PropertyFilter filter) {
    return this.userDao.findPage(pageable, filter);
  }

  public void delete(String... ids) {
    this.userDao.deleteAllInBatch(
        Arrays.stream(ids).map(id -> User.builder().id(id).build()).collect(Collectors.toList()));
  }

  public Optional<User> get(Long id) {
    return this.userDao.findById(id);
  }
}
