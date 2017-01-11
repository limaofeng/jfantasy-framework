package org.jfantasy.weixin.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.weixin.bean.App;
import org.springframework.stereotype.Repository;

@Repository
public class AppDao extends HibernateDao<App, String> {
}
