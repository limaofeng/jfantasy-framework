package org.jfantasy.weixin.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.weixin.bean.Fans;
import org.jfantasy.weixin.bean.UserKey;
import org.springframework.stereotype.Repository;

@Repository
public class FansDao extends HibernateDao<Fans, UserKey> {
}
