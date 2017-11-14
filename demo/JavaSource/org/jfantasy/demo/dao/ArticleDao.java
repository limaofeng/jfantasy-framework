package org.jfantasy.demo.dao;

import org.jfantasy.demo.bean.Article;
import org.jfantasy.framework.dao.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleDao extends HibernateDao<Article,Long> {
}
