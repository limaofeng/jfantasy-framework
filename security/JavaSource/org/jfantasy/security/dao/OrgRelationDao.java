package org.jfantasy.security.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.security.bean.OrgRelation;
import org.springframework.stereotype.Repository;

/**
 * Created by yhx on 2015/1/21.
 * 组织维度
 */
@Repository
public class OrgRelationDao extends HibernateDao<OrgRelation,Long> {
}
