package cn.asany.demo.service;

import cn.asany.demo.bean.Article;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.TestApplication;
import org.jfantasy.framework.search.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Slf4j
class ArticleServiceTest {

  @Autowired private ArticleService articleService;

  @BeforeEach
  void setUp() throws InterruptedException, JsonProcessingException {
    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
  }

  @AfterEach
  void tearDown() {}

  @Test
  void testSearch() throws IOException {
    Query query = Query.match("title", "大飞机");
    List<Article> articles = articleService.search(query, 20);
    assert !articles.isEmpty();
  }

  @Test
  void findAll() {
    articleService.findAll();
  }
}
