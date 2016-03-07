package org.jfantasy.system.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.system.bean.DictType;
import org.springframework.stereotype.Repository;

@Repository
public class DataDictionaryTypeDao extends HibernateDao<DictType,String> {
}
