package org.jfantasy.security.service;

import org.hibernate.criterion.Criterion;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.security.bean.Organization;
import org.jfantasy.security.dao.OrganizationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrganizationService {

    private final OrganizationDao organizationDao;

    @Autowired
    public OrganizationService(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public Organization get(String id) {
        return this.organizationDao.get(id);
    }

    public Pager<Organization> findPager(Pager<Organization> pager, List<PropertyFilter> filters) {
        return this.organizationDao.findPager(pager, filters);
    }

    public Organization save(Organization organization) {
        return this.organizationDao.save(organization);
    }

    public Organization findUnique(Criterion... criterions) {
        return this.organizationDao.findUnique(criterions);
    }

    public void delete(String... ids) {
        for (String id : ids) {
            this.organizationDao.delete(id);
        }
    }

    public Organization update(Organization organization, boolean patch) {
        return this.organizationDao.update(organization,patch);
    }

}
