package org.jfantasy.card.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.card.bean.Card;
import org.springframework.stereotype.Repository;

@Repository
public class CardDao extends HibernateDao<Card,String>{
}
