package org.jfantasy.demo.dao;

import org.jfantasy.demo.bean.Article;
import org.jfantasy.framework.dao.jpa.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleJPADao extends JpaRepository<Article,Long> {
}