package org.jfantasy.member.service;

import org.jfantasy.member.bean.Enterprise;
import org.jfantasy.member.dao.EnterpriseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnterpriseService {

    private final EnterpriseDao enterpriseDao;

    @Autowired
    public EnterpriseService(EnterpriseDao enterpriseDao) {
        this.enterpriseDao = enterpriseDao;
    }

    @Transactional
    public Enterprise save(Enterprise enterprise) {
        enterprise.setId(enterprise.getTeam().getKey());
        return this.enterpriseDao.save(enterprise, true);
    }

}
