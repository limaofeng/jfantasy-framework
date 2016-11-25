package org.jfantasy.logistics.dao;

import org.jfantasy.logistics.bean.Express;
import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class ExpressDao extends HibernateDao<Express, String> {

}
