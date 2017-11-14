package org.jfantasy.demo.dao;

import org.jfantasy.demo.bean.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleJPADao extends JpaRepository<Article,Long>, JpaSpecificationExecutor<Article> {
}
