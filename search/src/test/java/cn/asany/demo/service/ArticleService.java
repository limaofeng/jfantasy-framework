package cn.asany.demo.service;

import cn.asany.demo.dao.ArticleDao;
import cn.asany.demo.domain.Article;
import net.asany.jfantasy.framework.search.query.CuckooIndexSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService extends CuckooIndexSearcher<Article> {

  @Autowired private ArticleDao articleDao;

  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public Page<Article> findAll(int size) {
    return articleDao.findAll(Pageable.ofSize(size).withPage(1));
  }

  public void save(Article article) {
    if (this.articleDao.exists(Example.of(Article.builder().url(article.getUrl()).build()))) {
      return;
    }
    this.articleDao.save(article);
  }

  public void update(Long id, Article article) {
    article.setId(id);
    this.articleDao.save(article);
  }

  public void deleteById(Long id) {
    this.articleDao.deleteById(id);
  }
}
