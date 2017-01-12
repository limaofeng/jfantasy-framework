package org.jfantasy.sns.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.sns.bean.Platform;
import org.jfantasy.sns.bean.enums.PlatformType;
import org.jfantasy.sns.dao.PlatformDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatformService {

    private final PlatformDao platformDao;

    @Autowired
    public PlatformService(PlatformDao platformDao) {
        this.platformDao = platformDao;
    }

    @Transactional
    public Pager<Platform> findPager(Pager<Platform> pager, List<PropertyFilter> filters) {
        return this.platformDao.findPager(pager, filters);
    }

    @Transactional
    public Platform findUnique(PlatformType type, String appId) {
        return this.platformDao.findUnique(Restrictions.eq("type", type), Restrictions.eq("appId", appId));
    }

    @Transactional
    public Platform save(Platform platform) {
        return this.platformDao.save(platform);
    }

    @Transactional
    public Platform update(Platform platform, boolean patch) {
        return this.platformDao.update(platform, patch);
    }

    @Transactional
    public void deltele(Long id) {
        this.platformDao.delete(id);
    }

    @Transactional
    public Platform get(Long id) {
        return this.platformDao.get(id);
    }

}
