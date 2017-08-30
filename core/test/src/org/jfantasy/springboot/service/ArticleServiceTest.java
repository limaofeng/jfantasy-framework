package org.jfantasy.springboot.service;

import org.jfantasy.springboot.ApplicationTest;
import org.jfantasy.springboot.bean.Article;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("dev")
public class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    public void testSave() {
        Article article = new Article();
        article.setTitle("测试监听");
        article.setSummary("测试监听摘要");
        this.articleService.save(article);
        System.out.println(">>" + article.getId() + "\t" + article.getSn());

        article = new Article();
        article.setTitle("测试监听");
        article.setSummary("测试监听摘要");
        this.articleService.save(article);
        System.out.println(">>" + article.getId() + "\t" + article.getSn());
    }

}
