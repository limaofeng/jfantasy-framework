package org.jfantasy.security.service;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.security.SecurityServerApplication;
import org.jfantasy.security.bean.Employee;
import org.jfantasy.security.bean.Job;
import org.jfantasy.security.bean.Organization;
import org.jfantasy.security.bean.User;
import org.jfantasy.security.bean.enums.Sex;
import org.jfantasy.security.bean.enums.UserType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityServerApplication.class)
@ActiveProfiles("dev")
public class UserServiceTest {

    private UserService userService;

    @Test
    public void save() throws Exception {
        User user = new User();
        user.setUserType(UserType.employee);
        user.setUsername("15921884771");
        user.setPassword("123456");
        user.setNickName("李茂峰");
        user.setEnabled(true);

        Employee employee = new Employee();
        employee.setSn("100");
        employee.setBirthday(DateUtil.parse("1985-03-04","yyyy-MM-dd"));
        employee.setName("李茂峰");
        employee.setSex(Sex.male);
        employee.setEmail("limf@zbsg.com.cn");
        employee.setOrganization(new Organization());
        employee.getOrganization().setId("xx2");
        employee.setJob(new Job());
        employee.getJob().setId("web");
        user.setEmployee(employee);

        System.out.println(JSON.serialize(user));
        //this.userService.save(user);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}