package org.jfantasy.security.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.security.bean.Employee;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeDao extends HibernateDao<Employee,Long> {
}
