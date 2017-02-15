package org.jfantasy.security.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.security.bean.Employee;
import org.jfantasy.security.bean.Organization;
import org.jfantasy.security.dao.EmployeeDao;
import org.jfantasy.security.dao.OrganizationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrganizationService {

    private final OrganizationDao organizationDao;
    private final EmployeeDao employeeDao;

    @Autowired
    public OrganizationService(OrganizationDao organizationDao, EmployeeDao employeeDao) {
        this.organizationDao = organizationDao;
        this.employeeDao = employeeDao;
    }

    public Organization get(String id) {
        return this.organizationDao.get(id);
    }

    public List<Organization> findPager(List<PropertyFilter> filters) {
        return this.organizationDao.find(filters);
    }

    public Organization save(Organization organization) {
        return this.organizationDao.save(organization);
    }

    public Organization findUnique(Criterion... criterions) {
        return this.organizationDao.findUnique(criterions);
    }

    public void delete(String... ids) {
        for (String id : ids) {
            for (Employee employee : this.employeeDao.find(Restrictions.eq("organization.id", id))) {
                employee.setOrganization(null);
                this.employeeDao.update(employee);
            }
            this.organizationDao.delete(id);
        }
    }

    public Organization update(Organization organization, boolean patch) {
        return this.organizationDao.update(organization, patch);
    }

}
