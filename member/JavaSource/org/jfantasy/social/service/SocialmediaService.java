package org.jfantasy.social.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.social.bean.Socialmedia;
import org.jfantasy.social.dao.SocialmediaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocialmediaService {

    private final SocialmediaDao socialmediaDao;

    @Autowired
    public SocialmediaService(SocialmediaDao socialmediaDao) {
        this.socialmediaDao = socialmediaDao;
    }

    @Transactional
    public Pager<Socialmedia> findPager(Pager<Socialmedia> pager, List<PropertyFilter> filters) {
        return this.socialmediaDao.findPager(pager, filters);
    }

    @Transactional
    public Socialmedia save(Socialmedia socialmedia) {
        return this.socialmediaDao.save(socialmedia);
    }

    @Transactional
    public Socialmedia update(Socialmedia socialmedia, boolean patch) {
        return this.socialmediaDao.update(socialmedia, patch);
    }

    @Transactional
    public void deltele(Long id) {
        this.socialmediaDao.delete(id);
    }

    @Transactional
    public Socialmedia get(Long id) {
        return this.socialmediaDao.get(id);
    }

}
