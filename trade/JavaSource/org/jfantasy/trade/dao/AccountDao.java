package org.jfantasy.trade.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.trade.bean.Account;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDao extends HibernateDao<Account,String> {
}
