package org.jfantasy.card.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.card.bean.CardType;
import org.springframework.stereotype.Repository;

@Repository
public class CardTypeDao extends HibernateDao<CardType, String> {
}
