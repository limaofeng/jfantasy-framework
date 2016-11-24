package org.jfantasy.card.dao;

import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.jfantasy.card.bean.CardDesign;
import org.springframework.stereotype.Repository;

@Repository
public class CardDesignDao extends HibernateDao<CardDesign, String> {
}
