package cn.asany.example.demo.service;

import cn.asany.example.demo.bean.User;
import cn.asany.example.demo.dao.UserDao;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** @author limaofeng */
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
  public User save(User user) {
    return this.userDao.save(user);
  }

  public User update(Long id, boolean merge, User user) {
    user.setId(id);
    return this.userDao.update(user, merge);
  }

  public Page<User> findPage(Pageable pageable, List<PropertyFilter> filters) {
    return this.userDao.findPage(pageable, filters);
  }

  public void delete(Long... ids) {
    this.userDao.deleteAllInBatch(
        Arrays.stream(ids).map(id -> User.builder().id(id).build()).collect(Collectors.toList()));
  }

  public Optional<User> get(Long id) {
    return this.userDao.findById(id);
  }
}
