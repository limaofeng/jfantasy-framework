package org.jfantasy.sns.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.sns.bean.Platform;
import org.springframework.stereotype.Repository;

@Repository
public class PlatformDao extends HibernateDao<Platform,Long>{
}
