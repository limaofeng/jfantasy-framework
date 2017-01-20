package org.jfantasy.oauth.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.oauth.bean.ApiKey;
import org.jfantasy.oauth.bean.Application;
import org.jfantasy.oauth.dao.ApiKeyDao;
import org.jfantasy.oauth.dao.ApplicationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppService {

    private final ApplicationDao applicationDao;
    private final ApiKeyDao apiKeyDao;

    @Autowired
    public AppService(ApplicationDao applicationDao,ApiKeyDao apiKeyDao) {
        this.applicationDao = applicationDao;
        this.apiKeyDao = apiKeyDao;
    }

    @Transactional
    public Application save(Application application) {
        return this.applicationDao.save(application);
    }

    @Transactional
    public Pager<Application> findPager(Pager<Application> pager, List<PropertyFilter> filters) {
        return this.applicationDao.findPager(pager, filters);
    }

    @Transactional
    public void deltele(Long... ids) {
        for (Long id : ids) {
            this.applicationDao.delete(id);
        }
    }

    @Transactional
    public ApiKey save(Long id, ApiKey apiKey) {
        apiKey.setApplication(new Application());
        apiKey.getApplication().setId(id);
        return this.apiKeyDao.save(apiKey);
    }

    @Transactional
    public ApiKey get(String apiKey) {
        return this.apiKeyDao.get(apiKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKey> find(Long appid) {
        return this.apiKeyDao.find(Restrictions.eq("application.id",appid));
    }

}
