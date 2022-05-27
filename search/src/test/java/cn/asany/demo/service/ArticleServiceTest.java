package cn.asany.demo.service;

import cn.asany.demo.domain.Article;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.processor.WeiXinPageProcessor;
import org.jfantasy.framework.search.Highlighter;
import org.jfantasy.framework.search.TestApplication;
import org.jfantasy.framework.search.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.codecraft.webmagic.Spider;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Slf4j
class ArticleServiceTest {

  @Autowired private ArticleService articleService;

  @BeforeEach
  void setUp() throws InterruptedException {
    //    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    //    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void testSearch() {
    Query query = Query.match("title", "Java");
    List<Article> articles = articleService.search(query, 20);
    assert !articles.isEmpty();
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void testSearchPage() {
    Query query = Query.match("title", "Java");
    Page<Article> page = articleService.search(query, Pageable.ofSize(2).withPage(1));
    log.info("totalCount:" + page.getTotalElements());
    assert !page.isEmpty();
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void testSearchPageAndSort() {
    Query query = Query.match("title", "Java");
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    Page<Article> page = articleService.search(query, pageable);
    log.info("totalCount:" + page.getTotalElements());
    assert !page.isEmpty();
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void testSearchAndHighlight() {
    Query query = Query.match("title", "思维导图");
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    List<Article> articles =
        articleService.search(
            query,
            10,
            Highlighter.of(
                builder ->
                    builder.fields("title", b1 -> b1.preTags("<span>").postTags("</span>"))));

    for (Article article : articles) {
      log.info(article.getId() + "." + article.getTitle());
    }

    assert !articles.isEmpty();
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void findAll() {
    Page<Article> page = articleService.findAll(100);
    log.info("totalElements:" + page.getTotalElements());
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void save() {
    this.articleService.save(
        Article.builder()
            .url("http://localhost:8080/01")
            .author("赵四")
            .title("今天要坐飞机")
            .content("我的大摩托艇")
            .build());
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void update() {
    this.articleService.update(
        11606L,
        Article.builder()
            .id(11606L)
            .title("我们一定要有自己的大飞机")
            .author("limaofeng")
            .content(
                "国产大飞机，承载着几代人的中国梦。这一刻，梦想迎来新时代的回响。\n"
                    + "\n"
                    + "2014年5月23日，习近平总书记在上海考察期间，专程来到中国商飞设计研发中心，登上C919大型客机展示样机，坐在驾驶舱主驾驶的座位上，了解速度表、高度表、航迹图等有关仪器仪表情况。\n"
                    + "\n"
                    + "“我们要做一个强国，就一定要把装备制造业搞上去，把大飞机搞上去，起带动作用、标志性作用。”习近平总书记的话坚定有力。\n"
                    + "\n"
                    + "大厅里，人们闻讯赶来。\n"
                    + "\n"
                    + "时任数控机加车间钳工组组长的胡双钱，对当时的情景记忆犹新：“总书记和我们前排的设计师、工人、试飞员等一一握手，虽然时间短暂，但我终生难忘。”\n"
                    + "\n"
                    + "让胡双钱和同事们深受触动的是，习近平总书记在现场看得细，问得也细。在驾驶舱，他详细了解有关设计情况。穿过飞机客舱时，他两次在座位上坐下来感受感受。\n"
                    + "\n"
                    + "习近平总书记还对大家说，中国是最大的飞机市场，过去有人说造不如买、买不如租，这个逻辑要倒过来，要花更多资金来研发、制造自己的大飞机。\n"
                    + "\n"
                    + "“我深切地体会到，国家是多么重视大飞机项目。”胡双钱深感振奋，“我们要力争早日让自主研制的大型客机在蓝天上自由翱翔。”\n"
                    + "\n"
                    + "作为我国自主研制的大型喷气式客机，C919可搭乘超过150名乘客，航程可达5500公里。国内有22个省份、200多家企业、36所高校、数十万产业人员参与研制。\n"
                    + "\n"
                    + "这架飞机上凝聚的，不仅有突破创新的中国智慧，更有坚持梦想的国家意志。\n"
                    + "\n"
                    + "在多个场合，习近平总书记一再强调坚持科技自立自强。他反复指出，关键核心技术“要不来、买不来、讨不来”。\n"
                    + "\n"
                    + "从“天问一号”探访火星，到“奋斗者”号深潜马里亚纳海沟；从“北斗”组网、“复兴号”飞驰，到快速研制新冠病毒检测试剂和高水平疫苗……一个个“中国印记”铭刻在攀登科技高峰的征途上，没有辜负习近平总书记的殷切期望。\n"
                    + "\n"
                    + "一个时代有一个时代的担当。\n"
                    + "\n"
                    + "“大型客机研发和生产制造能力是一个国家航空水平的重要标志，也是一个国家整体实力的重要标志。”\n"
                    + "\n"
                    + "“中国飞机制造业走过了一段艰难、坎坷、曲折的历程，现在是而今迈步从头越，势头很好，开局很好……”\n"
                    + "\n"
                    + "习近平总书记对国产大飞机的寄望之高、关切之深，让在场工作人员既惊喜兴奋，也深感重任在肩。\n"
                    + "\n"
                    + "时任中国商飞上海飞机设计研究院副院长的李东升回忆：“总书记的每一句话都在我们心中引起了极大的共鸣，大家报以一阵又一阵热烈的掌声。”\n"
                    + "\n"
                    + "当总书记步出综合试验大厅时，许多年轻人自发跟随着，不舍得离开。\n"
                    + "\n"
                    + "“我寄厚望于你们。中国大飞机事业万里长征走了又一步，我们一定要有自己的大飞机！”现场爆发出雷鸣般的掌声，不少上了年纪的科研人员热泪盈眶。\n"
                    + "\n"
                    + "这次考察后第3年，2017年5月5日，C919翱翔蓝天，成功首飞。中国大飞机之路，行稳致远！\n"
                    + "\n"
                    + "时间是忠实的记录者，镌刻着执政者奋进的步伐。4月18日起，新华社开设“近镜头•温暖的瞬间”栏目，精心选取党的十八大以来，习近平总书记治国理政的精彩瞬间，讲述每一张照片背后的温暖故事。")
            .build());
  }

  @Test
  @Timeout(value = 30, unit = TimeUnit.SECONDS)
  void deleteById() {
    this.articleService.deleteById(4L);
  }

  void downloadArticles() {
    Spider.create(new WeiXinPageProcessor(this.articleService))
        .addUrl("https://mp.weixin.qq.com/s/mO7HCSsLCtCghBpCeY1OeQ")
        .thread(10)
        .run();
  }
}
