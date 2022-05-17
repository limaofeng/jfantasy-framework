package cn.asany.demo.service;

import cn.asany.demo.bean.Article;
import cn.asany.demo.dao.ArticleDao;
import java.util.List;
import org.jfantasy.framework.search.query.CuckooIndexSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService extends CuckooIndexSearcher<Article> {

  @Autowired private ArticleDao articleDao;

  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public List<Article> findAll() {
    return articleDao.findAll();
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
