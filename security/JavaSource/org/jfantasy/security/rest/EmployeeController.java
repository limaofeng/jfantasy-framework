package org.jfantasy.security.rest;


import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.security.bean.Employee;
import org.jfantasy.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final UserService userService;

    @Autowired
    public EmployeeController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询用户
     *
     * @param pager   Pager
     * @param filters Filters
     * @return Pager<Employee>
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Employee> search(Pager<Employee> pager, List<PropertyFilter> filters) {
        return this.userService.findEmployeePager(pager, filters);
    }

}
