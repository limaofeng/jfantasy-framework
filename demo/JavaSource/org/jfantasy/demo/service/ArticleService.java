package org.jfantasy.demo.service;

import org.jfantasy.demo.bean.Article;
import org.jfantasy.demo.dao.ArticleJPADao;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleJPADao articleDao;

    @Transactional
    public Article save(Article article) {
        return this.articleDao.save(article);//this.articleDao.save(article);
    }

    @Transactional
    public Iterable<Article> findAll() {
        return this.articleDao.findAll();
    }

    @Transactional
    public Pager<Article> findPager(Pager<Article> pager, List<PropertyFilter> filters) {
        return articleDao.findPager(pager, filters);
    }

    public Article get(Long id) {
        return this.articleDao.findOne(id);
    }
}
