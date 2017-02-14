package org.jfantasy.security.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.security.bean.Job;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.dao.JobDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class JobService {

    private final JobDao jobDao;
    private RoleService roleService;

    @Autowired
    public JobService(JobDao jobDao) {
        this.jobDao = jobDao;
    }

    public Pager<Job> findPager(Pager<Job> pager, List<PropertyFilter> filters) {
        return this.jobDao.findPager(pager, filters);
    }

    public List<Job> find(List<PropertyFilter> filters) {
        return this.jobDao.find(filters);
    }

    public List<Job> find(Criterion... criterions) {
        return this.jobDao.find(criterions);
    }

    public Job save(Job job) {
        return this.jobDao.save(job);
    }

    public Job get(String id) {
        return this.jobDao.get(id);
    }

    public void delete(String... ids) {
        for (String id : ids) {
            this.jobDao.delete(id);
        }
    }

    public Job findUnique(String id) {
        return this.jobDao.findUnique(Restrictions.eq("id", id));
    }

    public Job update(Job job, boolean patch) {
        return this.jobDao.update(job, patch);
    }

    public List<Role> addRoles(String id, boolean clear, String[] roles) {
        Job job = this.jobDao.get(id);
        if (clear) {
            job.getRoles().clear();
        }
        for (String role : roles) {
            if (!ObjectUtil.exists(job.getRoles(), "id", role)) {
                job.getRoles().add(this.roleService.get(role));
            }
        }
        this.jobDao.update(job);
        return job.getRoles();
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public List<Role> removeRoles(String id, String... roles) {
        Job job = this.jobDao.get(id);
        for (String role : roles) {
            ObjectUtil.remove(job.getRoles(), "id", role);
        }
        this.jobDao.update(job);
        return job.getRoles();
    }
}
