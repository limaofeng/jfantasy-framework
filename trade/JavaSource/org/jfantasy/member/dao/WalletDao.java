package org.jfantasy.member.dao;


import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.member.bean.Wallet;
import org.springframework.stereotype.Repository;

@Repository
public class WalletDao extends HibernateDao<Wallet, Long> {

}
