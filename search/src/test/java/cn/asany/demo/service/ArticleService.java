/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
