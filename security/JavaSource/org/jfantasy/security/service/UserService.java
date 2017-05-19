package org.jfantasy.security.service;

import org.hibernate.Hibernate;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.LoginException;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.regexp.RegexpConstant;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.security.bean.Employee;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.bean.User;
import org.jfantasy.security.bean.UserDetails;
import org.jfantasy.security.bean.enums.EmployeeStatus;
import org.jfantasy.security.bean.enums.UserType;
import org.jfantasy.security.context.LoginEvent;
import org.jfantasy.security.context.LogoutEvent;
import org.jfantasy.security.dao.EmployeeDao;
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
    private final EmployeeDao employeeDao;

    private ApplicationContext applicationContext;
    private PasswordEncoder passwordEncoder;
    private RoleService roleService;

    @Autowired
    public UserService(UserDao userDao, EmployeeDao employeeDao) {
        this.userDao = userDao;
        this.employeeDao = employeeDao;
    }

    /**
     * 保存用户
     *
     * @param user 用户对象
     */
    @CacheEvict(value = {"fantasy.security.userService"}, allEntries = true)
    public User save(User user) {
        if (UserType.admin == user.getUserType()) {
            UserDetails details = ObjectUtil.defaultValue(user.getDetails(), new UserDetails());
            // 如果用email注册
            if (RegexpUtil.isMatch(user.getUsername(), RegexpConstant.VALIDATOR_EMAIL)) {
                details.setEmail(user.getUsername());
            }
            // 如果用手机注册
            if (RegexpUtil.isMatch(user.getUsername(), RegexpConstant.VALIDATOR_MOBILE)) {
                details.setMobile(user.getUsername());
            }
            user.setDetails(details);
        } else if (UserType.employee == user.getUserType()) {
            Employee employee = ObjectUtil.defaultValue(user.getEmployee(), new Employee());
            employee.setStatus(EmployeeStatus.work);
            // 如果用email注册
            if (RegexpUtil.isMatch(user.getUsername(), RegexpConstant.VALIDATOR_EMAIL)) {
                employee.setEmail(user.getUsername());
            }
            // 如果用手机注册
            if (RegexpUtil.isMatch(user.getUsername(), RegexpConstant.VALIDATOR_MOBILE)) {
                employee.setMobile(user.getUsername());
            }
            user.setEmployee(employee);
        }

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

    public User update(User user, boolean patch) {
        if (UserType.admin == user.getUserType()) {
            user.setEmployee(null);
        } else if (UserType.employee == user.getUserType()) {
            user.setDetails(null);
        }
        return this.userDao.update(user, patch);
    }

    public User changePassword(Long id, String oldPassword, String newPassword) {
        User user = this.userDao.get(id);
        if (user == null) {
            throw new NotFoundException("用户不存在");
        }
        if (!user.getEnabled()) {
            throw new ValidationException(100101, "用户已被禁用");
        }
        if (!this.passwordEncoder.matches(user.getPassword(), oldPassword)) {
            throw new ValidationException(100102, "提供的 password token 不正确!");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.userDao.update(user);
    }

    //@Cacheable(value = "fantasy.security.userService", key = "'findUniqueByUsername' + #username ")
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

    public User login(String username, String password) {
        User user = this.userDao.findUniqueBy("username", username);

        if (user == null) {//用户不存在
            throw new ValidationException(100202, "用户名和密码错误");
        }
        if (!this.passwordEncoder.matches(user.getPassword(), password)) {
            throw new ValidationException(100201, "用户名和密码错误");
        }
        if (!user.getEnabled()) {
            throw new LoginException("用户被禁用");
        }
        if (!user.getAccountNonLocked()) {
            throw new LoginException("用户被锁定");
        }
        user.setLastLoginTime(DateUtil.now());
        this.userDao.save(user);
        Hibernate.initialize(user.getRoles());
        Hibernate.initialize(user.getUserGroups());
        this.applicationContext.publishEvent(new LoginEvent(user));
        return user;
    }

    public void logout(String username) {
        this.applicationContext.publishEvent(new LogoutEvent(findUniqueByUsername(username)));
    }

    public List<Role> addRoles(Long id, boolean clear, String[] roles) {
        User user = this.userDao.get(id);
        if (clear) {
            user.getRoles().clear();
        }
        for (String role : roles) {
            if (!ObjectUtil.exists(user.getRoles(), "id", role)) {
                user.getRoles().add(this.roleService.get(role));
            }
        }
        this.userDao.update(user);
        return user.getRoles();
    }

    public Pager<Employee> findEmployeePager(Pager<Employee> pager, List<PropertyFilter> filters) {
        return this.employeeDao.findPager(pager, filters);
    }

    public List<Role> removeRoles(Long id, String... roles) {
        User user = this.userDao.get(id);
        for (String role : roles) {
            ObjectUtil.remove(user.getRoles(), "id", role);
        }
        this.userDao.update(user);
        return user.getRoles();
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}