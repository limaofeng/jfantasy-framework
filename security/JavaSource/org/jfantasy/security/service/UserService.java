package org.jfantasy.security.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.service.PasswordTokenEncoder;
import org.jfantasy.framework.service.PasswordTokenType;
import org.jfantasy.framework.spring.mvc.error.LoginException;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.regexp.RegexpCst;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.security.bean.User;
import org.jfantasy.security.bean.UserDetails;
import org.jfantasy.security.context.LoginEvent;
import org.jfantasy.security.context.LogoutEvent;
import org.jfantasy.security.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserDao userDao;

    private ApplicationContext applicationContext;
    private PasswordEncoder passwordEncoder;
    private PasswordTokenEncoder passwordTokenEncoder;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 保存用户
     *
     * @param user 用户对象
     */
    @CacheEvict(value = {"fantasy.security.userService"}, allEntries = true)
    public User save(User user) {
        UserDetails details = ObjectUtil.defaultValue(user.getDetails(), new UserDetails());
        // 如果用email注册
        if (RegexpUtil.isMatch(user.getUsername(), RegexpCst.VALIDATOR_EMAIL)) {
            details.setEmail(user.getUsername());
        }
        // 如果用手机注册
        if (RegexpUtil.isMatch(user.getUsername(), RegexpCst.VALIDATOR_MOBILE)) {
            details.setMobile(user.getUsername());
        }
        user.setDetails(details);

        // 默认昵称与用户名一致
        if (StringUtil.isBlank(user.getNickName())) {
            user.setNickName(user.getUsername());
        }

        // 初始化用户权限
        user.setRoles(new ArrayList<>());
        user.setUserGroups(new ArrayList<>());
        //初始化用户状态
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        // 保存用户
        return this.userDao.save(user);
    }

    public User changePassword(Long id, PasswordTokenType type, String oldPassword, String newPassword) {
        User user = this.userDao.get(id);
        if (user == null) {
            throw new NotFoundException("用户不存在");
        }
        if (!user.isEnabled()) {
            throw new ValidationException(201.1f, "用户已被禁用");
        }
        if (!this.passwordTokenEncoder.matches("login", type, user.getUsername(), user.getPassword(), oldPassword)) {
            throw new ValidationException(203.1f, "提供的 password token 不正确!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.userDao.update(user);
    }

    @Cacheable(value = "fantasy.security.userService", key = "'findUniqueByUsername' + #username ")
    public User findUniqueByUsername(String username) {
        return this.userDao.findUniqueBy("username", username);
    }

    public Pager<User> findPager(Pager<User> pager, List<PropertyFilter> filters) {
        return this.userDao.findPager(pager, filters);
    }

    @CacheEvict(value = {"fantasy.security.userService"}, allEntries = true)
    public void delete(Long... ids) {
        for (Long id : ids) {
            this.userDao.delete(id);
        }
    }

    public User get(Long id) {
        return this.userDao.get(id);
    }

    public User login(PasswordTokenType type, String username, String password) {
        User user = this.userDao.findUniqueBy("username", username);

        if (!this.passwordTokenEncoder.matches("login", type, username, user != null ? user.getPassword() : "", password)) {
            throw new ValidationException(203.1f, "用户名和密码错误");
        }

        if (user == null) {//用户不存在
            throw new ValidationException(203.1f, "用户名和密码错误");
        }

        if (!user.isEnabled()) {
            throw new LoginException("用户被禁用");
        }
        if (!user.isAccountNonLocked()) {
            throw new LoginException("用户被锁定");
        }
        user.setLastLoginTime(DateUtil.now());
        this.userDao.save(user);
        this.applicationContext.publishEvent(new LoginEvent(user));
        return user;
    }

    public void logout(String username) {
        this.applicationContext.publishEvent(new LogoutEvent(findUniqueByUsername(username)));
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setPasswordTokenEncoder(PasswordTokenEncoder passwordTokenEncoder) {
        this.passwordTokenEncoder = passwordTokenEncoder;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}