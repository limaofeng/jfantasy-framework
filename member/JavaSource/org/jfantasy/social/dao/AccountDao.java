package org.jfantasy.social.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.social.bean.Account;
import org.springframework.stereotype.Repository;

//connect
@Repository
public class AccountDao extends HibernateDao<Account, Long> {


}
