package org.jfantasy.trade.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.dao.ProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Transactional
    public Project get(String key) {
        return projectDao.get(key);
    }

    @Transactional
    public void save(Project project) {
        this.projectDao.save(project);
    }

    @Transactional
    public List<Project> find(ProjectType... projectTypes) {
        return this.projectDao.find(Restrictions.in("type",projectTypes));
    }
}
